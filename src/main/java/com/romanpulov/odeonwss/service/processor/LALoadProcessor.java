package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.CompositionService;
import com.romanpulov.odeonwss.service.processor.parser.CueParser;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LALoadProcessor extends AbstractArtistProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LALoadProcessor.class);


    private final CompositionService compositionService;

    private final MediaParser mediaParser;

    public LALoadProcessor(
            ArtistRepository artistRepository,
            ArtifactRepository artifactRepository,
            CompositionService compositionService,
            MediaParser mediaParser
    ) {
        super(artistRepository, artifactRepository);
        this.compositionService = compositionService;
        this.mediaParser = mediaParser;
    }

    @Override
    protected ArtifactType getArtifactType() {
        return ArtifactType.withLA();
    }

    @Override
    protected void processCompositionsPath(Path path, Artifact artifact) throws ProcessorException {
        processCompositionsPathWithDiskNum(path, artifact, 0);
    }

    private void processCompositionsPathWithDiskNum(Path path, Artifact artifact, int diskNum) throws ProcessorException {
        logger.debug("Processing composition path:" + path + " with artifact:" + artifact);

        List<Path> directoryPaths;
        try (Stream<Path> stream = Files.list(path)) {
            directoryPaths = stream.collect(Collectors.toList());
        } catch (IOException e) {
            throw new ProcessorException(ProcessorMessages.ERROR_PROCESSING_FILES, e.getMessage());
        }

        List<Path> directoryFolderPaths = directoryPaths
                .stream()
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        Map<Path, Integer> directoryFolderDisksPaths = directoryFolderPaths
                .stream()
                .collect(Collectors.toMap(p -> p, p -> NamesParser.getDiskNumFromFolderName(p.getFileName().toString())))
                .entrySet()
                .stream()
                .filter(v -> v.getValue() > 0)
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        if (directoryFolderDisksPaths.size() > 0) {
            if (directoryFolderPaths.size() > directoryFolderDisksPaths.size()) {
                errorHandler(ProcessorMessages.ERROR_FOLDER_WITH_DISK_NUMBERS_CONTAINS_OTHER, path.toString());
            } else {
                for (Map.Entry<Path, Integer> entry: directoryFolderDisksPaths.entrySet()) {
                    processCompositionsPathWithDiskNum(entry.getKey(), artifact, entry.getValue());
                }
            }
        } else {
            CompositionsSummary summary = null;

            List<String> directoryFileNames = directoryPaths
                    .stream()
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());

            //validation for Cue
            List<Path> cuePaths = directoryPaths.stream().filter(p -> p.toString().endsWith("cue")).collect(Collectors.toList());
            if (cuePaths.size() > 0) {
                logger.debug("Cue files found, processing");
                boolean cueProcessed = false;
                for (Path cuePath : cuePaths) {
                    List<CueParser.CueTrack> cueTracks = CueParser.parseFile(cuePath);
                    List<String> cueFiles = cueTracks
                            .stream()
                            .map(CueParser.CueTrack::getFileName)
                            .distinct().collect(Collectors.toList());

                    if (new HashSet<>(directoryFileNames).containsAll(cueFiles)) {
                        logger.debug("Contains all for " + cuePath.toString());
                        summary = processCueFile(path, artifact, directoryPaths, cuePath, cueTracks, cueFiles, diskNum);
                        cueProcessed = true;
                    }
                }
                if (!cueProcessed) {
                    errorHandler(ProcessorMessages.ERROR_FILES_IN_CUE_NOT_FOUND, path.toString());
                }
            } else {
                logger.debug("Cue files not found, processing files");
                //no cue files,
                summary = processCompositions(path, artifact, directoryPaths, diskNum);
            }

            if (summary == null) {
                errorHandler(ProcessorMessages.ERROR_NO_DATA_FOR_FOLDER, path.toString());
            } else {
                artifact.setDuration(summary.duration);
                artifact.setSize(summary.size);
                artifact.setInsertDate(LocalDate.now());
                artifactRepository.save(artifact);
            }
        }
    }

    private CompositionsSummary processCueFile(
            Path path,
            Artifact artifact,
            List<Path> directoryPaths,
            Path cuePath,
            List<CueParser.CueTrack> cueTracks,
            List<String> cueFiles,
            int diskNum
    ) throws ProcessorException {
        CompositionsSummary summary = new CompositionsSummary();

        logger.debug("Processing Cue");

        Set<String> cueFileNames = cueTracks.stream().map(CueParser.CueTrack::getFileName).collect(Collectors.toSet());

        // get only files which are compositions
        List<Path> compositionPaths = directoryPaths
                .stream()
                .filter(p -> cueFileNames.contains(p.getFileName().toString()))
                .collect(Collectors.toList());

        Map<String, MediaFileInfo> parsedCompositionMediaInfo = mediaParser.parseCompositions(compositionPaths);

        Map<String, MediaFile> mediaFiles = new HashMap<>();
        List<Composition> compositions = new ArrayList<>();

        logger.debug("Composition paths: " + compositionPaths);

        int firstSection = 0;
        for (int i = 0; i < cueTracks.size(); i++) {
            CueParser.CueTrack cueTrack = cueTracks.get(i);
            boolean lastTrack = i == cueTracks.size() - 1;
            CueParser.CueTrack nextCueTrack = lastTrack ? null : cueTracks.get(i + 1);

            if (i == 0) {
                firstSection = cueTrack.getSection();
            }

            String fileName = cueTrack.getFileName();

            MediaFileInfo mediaFileInfo = parsedCompositionMediaInfo.get(fileName);
            if (mediaFileInfo == null) {
                throw new ProcessorException(ProcessorMessages.ERROR_NO_DATA_FOR_FILE, cueTrack.getFileName());
            } else {
                MediaFile mediaFile;
                if (mediaFiles.containsKey(fileName)) {
                    mediaFile = mediaFiles.get(fileName);
                } else {
                    mediaFile = MediaFileMapper.fromMediaFileInfo(mediaFileInfo);
                    mediaFile.setArtifact(artifact);
                    mediaFiles.put(fileName, mediaFile);
                    summary.size += mediaFile.getSize();
                }

                logger.debug("MediaFile saved: " + mediaFile);
                Composition composition = new Composition();
                composition.setArtifact(artifact);
                composition.setTitle(cueTrack.getTitle());

                // disk num
                long compositionDiskNum = diskNum > 0 ?
                        diskNum :
                        Integer.valueOf(NamesParser.getDiskNumFromFileName(cuePath.getFileName().toString())).longValue();
                composition.setDiskNum(compositionDiskNum);

                composition.setNum(Integer.valueOf(cueTrack.getNum()).longValue());

                // duration
                long mediaDuration = mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration();
                long cueDuration;
                if (nextCueTrack != null) {
                    cueDuration = nextCueTrack.getSection() - cueTrack.getSection();
                } else {
                    cueDuration = mediaDuration - cueTrack.getSection() + firstSection;
                }
                long duration = cueDuration > 0 ? cueDuration : mediaDuration;
                composition.setDuration(duration);
                summary.duration += duration;

                composition.getMediaFiles().add(mediaFile);

                compositions.add(composition);
            }
        }

        compositionService.insertCompositionsWithMedia(compositions, mediaFiles.values());
        return summary;
    }

    CompositionsSummary processCompositions(
            Path path,
            Artifact artifact,
            List<Path> directoryPaths,
            int diskNum
    ) throws ProcessorException {
        CompositionsSummary summary = new CompositionsSummary();

        Map<String, NamesParser.NumberTitle> parsedCompositionFileNames = parseCompositionFileNames(directoryPaths);
        if (parsedCompositionFileNames.size() > 0) {
            List<MediaFile> mediaFiles = new ArrayList<>();
            List<Composition> compositions = new ArrayList<>();

            List<Path> directoryCompositionsPaths = directoryPaths
                    .stream()
                    .filter(p -> parsedCompositionFileNames.containsKey(p.getFileName().toString()))
                    .collect(Collectors.toList());
            Map<String, MediaFileInfo> parsedCompositionMediaInfo = mediaParser.parseCompositions(directoryCompositionsPaths);

            for (Map.Entry<String, NamesParser.NumberTitle> compositionFile: parsedCompositionFileNames.entrySet()) {
                MediaFileInfo mediaFileInfo = parsedCompositionMediaInfo.get(compositionFile.getKey());
                MediaFile mediaFile = MediaFileMapper.fromMediaFileInfo(mediaFileInfo);
                mediaFile.setArtifact(artifact);

                mediaFiles.add(mediaFile);

                Composition composition = new Composition();
                composition.setArtifact(artifact);
                composition.setTitle(compositionFile.getValue().getTitle());

                int compositionDiskNum = diskNum == 0 ? 1 : diskNum;
                composition.setDiskNum(Integer.valueOf(compositionDiskNum).longValue());

                composition.setNum(compositionFile.getValue().getNumber());
                composition.setDuration(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration());

                composition.getMediaFiles().add(mediaFile);

                compositions.add(composition);

                summary.duration += mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration();
                summary.size += mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize();
            }

            compositionService.insertCompositionsWithMedia(compositions, mediaFiles);
        } else {
            errorHandler(ProcessorMessages.ERROR_NO_DATA_FOR_FOLDER, path.toString());
        }

        return summary;
    }

    private Map<String, NamesParser.NumberTitle> parseCompositionFileNames(List<Path> compositionPaths) {
        Map<String, NamesParser.NumberTitle> result = new HashMap<>();

        for (Path path: compositionPaths) {
            String compositionFileName = path.getFileName().toString();
            NamesParser.NumberTitle nt = NamesParser.parseMusicComposition(compositionFileName);
            if (nt != null) {
                result.put(compositionFileName, nt);
            }
        }

        return result;
    }
}
