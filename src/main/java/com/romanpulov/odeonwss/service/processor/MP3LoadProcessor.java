package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.MediaFileParserInterface;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MP3LoadProcessor extends AbstractFileSystemProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MP3LoadProcessor.class);

    private final ArtistRepository artistRepository;

    private final ArtifactRepository artifactRepository;

    private final CompositionRepository compositionRepository;

    private final MediaFileRepository mediaFileRepository;

    private final MediaFileParserInterface mediaFileParser;

    private static class CompositionsSummary {
        private long duration;
        private long size;
    }

    public MP3LoadProcessor(
            ArtistRepository artistRepository,
            ArtifactRepository artifactRepository,
            CompositionRepository compositionRepository,
            MediaFileRepository mediaFileRepository,
            MediaFileParserInterface mediaFileParser ) {
        this.artistRepository = artistRepository;
        this.artifactRepository = artifactRepository;
        this.compositionRepository = compositionRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.mediaFileParser = mediaFileParser;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        try (Stream<Path> stream = Files.list(path)){
            for (Path p : stream.collect(Collectors.toList())) {
                logger.debug("Path:" + p.getFileName());
                processArtistsPath(p);
            }
        } catch (IOException e) {
            throw new ProcessorException(ProcessorMessages.ERROR_EXCEPTION,  e.getMessage());
        }
    }

    private void processArtistsPath(Path path) throws ProcessorException {
        if (!Files.isDirectory(path)) {
            errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, path.getFileName());
            return;
        }

        String artistName = path.getFileName().toString();

        Optional<Artist> artist = artistRepository.findFirstByTypeAndName(ArtistTypes.A.name(), artistName);
        if (artist.isEmpty()) {
            warningHandlerWithAddArtistAction(String.format(ProcessorMessages.ERROR_ARTIST_NOT_FOUND, artistName), artistName);
            return;
        }

        try (Stream<Path> stream = Files.list(path)){
            for (Path p: stream.collect(Collectors.toList())) {
                logger.debug("File:" + p.getFileName());
                processArtifactsPath(p, artist.get());
            }
        } catch (IOException e) {
            throw new ProcessorException("Error processing files: " + e.getMessage());
        }
    }

    private void processArtifactsPath(Path path, Artist artist) throws ProcessorException {
        if (!Files.isDirectory(path)) {
            errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, path.getFileName());
            return;
        }

        String artifactName = path.getFileName().toString();

        NamesParser.YearTitle yt = NamesParser.parseMusicArtifactTitle(artifactName);
        if (yt == null) {
            errorHandler(ProcessorMessages.ERROR_PARSING_ARTIFACT_NAME, path.toAbsolutePath().getFileName());
            return;
        }

        Artifact artifact = new Artifact();
        artifact.setArtifactType(ArtifactType.withMP3());
        artifact.setArtist(artist);
        artifact.setTitle(yt.getTitle());
        artifact.setYear((long) yt.getYear());

        Optional<Artifact> existingArtifact = artifactRepository.findFirstByArtifactTypeAndArtistAndTitleAndYear(
                artifact.getArtifactType(),
                artifact.getArtist(),
                artifact.getTitle(),
                artifact.getYear()
        );

        if (existingArtifact.isEmpty()) {

            artifactRepository.save(artifact);

            List<Path> compositionPaths;
            try (Stream<Path> stream = Files.list(path)) {
                compositionPaths = stream.collect(Collectors.toList());
            } catch (IOException e) {
                throw new ProcessorException(ProcessorMessages.ERROR_PROCESSING_FILES, e.getMessage());
            }

            Map<String, NamesParser.NumberTitle> parsedCompositionFileNames = parseCompositionFileNames(compositionPaths);
            if (parsedCompositionFileNames != null) {
                Map<String, MediaFileInfo> parsedCompositionMediaInfo = parseCompositionsMediaInfo(compositionPaths);
                if (parsedCompositionMediaInfo != null) {
                    CompositionsSummary summary = saveCompositionsAndMediaFiles(artifact, compositionPaths, parsedCompositionFileNames, parsedCompositionMediaInfo);

                    artifact.setDuration(summary.duration);
                    artifact.setSize(summary.size);
                    artifact.setInsertDate(LocalDate.now());
                    artifactRepository.save(artifact);
                }
            }
        }
    }

    private Map<String, NamesParser.NumberTitle> parseCompositionFileNames(List<Path> compositionPaths) {
        Map<String, NamesParser.NumberTitle> result = new HashMap<>();

        for (Path path: compositionPaths) {
            if (Files.isDirectory(path)) {
                errorHandler(ProcessorMessages.ERROR_EXPECTED_FILE,  path.toAbsolutePath());
                return null;
            }

            String compositionFileName = path.getFileName().toString();
            if (!compositionFileName.endsWith("mp3")) {
                errorHandler(ProcessorMessages.ERROR_WRONG_FILE_TYPE, path.toAbsolutePath());
                return null;
            }

            NamesParser.NumberTitle nt = NamesParser.parseMusicComposition(compositionFileName);
            if (nt == null) {
                errorHandler(ProcessorMessages.ERROR_PARSING_COMPOSITION_NAME, path.toAbsolutePath().getFileName());
                return null;
            }

            result.put(compositionFileName, nt);
        }

        return result;
    }

    private Map<String, MediaFileInfo> parseCompositionsMediaInfo(List<Path> compositionPaths)
            throws ProcessorException {
        List<Callable<Pair<String, MediaFileInfo>>> callables = new ArrayList<>();

        for (Path path: compositionPaths) {
            Callable<Pair<String, MediaFileInfo>> callable = () -> {
                MediaFileInfo mediafileInfo = mediaFileParser.parseMediaFile(path);
                return Pair.of(path.getFileName().toString(), mediafileInfo);
            };
            callables.add(callable);
        }

        List<Future<Pair<String, MediaFileInfo>>> futures;
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            futures = executorService.invokeAll(callables);
        } catch (InterruptedException e) {
            throw new ProcessorException("Error executing callables for media file info retrieval:" + e.getMessage());
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        Map<String, MediaFileInfo> result = new HashMap<>();

        try {
            for (Future<Pair<String, MediaFileInfo>> future : futures) {
                Pair<String, MediaFileInfo> futureData = future.get();
                result.put(futureData.getFirst(), futureData.getSecond());
            }
        } catch (ExecutionException | InterruptedException e) {
            errorHandler(ProcessorMessages.ERROR_PARSING_FILE, e.getMessage());
            return null;
        }

        return result;
    }

    private CompositionsSummary saveCompositionsAndMediaFiles (
            Artifact artifact,
            List<Path> compositionPaths,
            Map<String, NamesParser.NumberTitle> parsedCompositionNames,
            Map<String, MediaFileInfo> parsedCompositionsMediaInfo
            ) throws ProcessorException {

        CompositionsSummary summary = new CompositionsSummary();

        for (Path path: compositionPaths) {
            String fileName = path.getFileName().toString();

            NamesParser.NumberTitle nt = parsedCompositionNames.get(fileName);
            MediaFileInfo mediaFileInfo = parsedCompositionsMediaInfo.get(fileName);
            if ((nt == null) || (mediaFileInfo == null)) {
                throw new ProcessorException(ProcessorMessages.ERROR_NO_DATA_FOR_FILE, fileName);
            } else {
                Composition composition = new Composition();
                composition.setArtifact(artifact);
                composition.setTitle(nt.getTitle());
                composition.setDiskNum(1L);
                composition.setNum(nt.getNumber());
                composition.setDuration(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration());

                compositionRepository.save(composition);

                MediaFile mediaFile = new MediaFile();
                mediaFile.setComposition(composition);
                mediaFile.setName(fileName);
                mediaFile.setFormat(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getFormatName());
                mediaFile.setSize(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize());
                mediaFile.setBitrate(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getBitRate());
                mediaFile.setDuration(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration());

                mediaFileRepository.save(mediaFile);

                summary.duration += mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration();
                summary.size += mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize();
            }
        }
        return summary;
    }

}
