package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MP3LoadProcessor extends AbstractArtistProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MP3LoadProcessor.class);

    private final CompositionRepository compositionRepository;

    private final MediaFileRepository mediaFileRepository;

    private final MediaParser mediaParser;

    public MP3LoadProcessor(
            ArtistRepository artistRepository,
            ArtifactRepository artifactRepository,
            CompositionRepository compositionRepository,
            MediaFileRepository mediaFileRepository,
            MediaParser mediaParser )
    {
        super(artistRepository, artifactRepository);
        this.compositionRepository = compositionRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.mediaParser = mediaParser;
    }

    @Override
    protected void processCompositionsPath(Path path, Artifact artifact) throws ProcessorException {
        List<Path> compositionPaths;
        try (Stream<Path> stream = Files.list(path)) {
            compositionPaths = stream.collect(Collectors.toList());
        } catch (IOException e) {
            throw new ProcessorException(ProcessorMessages.ERROR_PROCESSING_FILES, e.getMessage());
        }

        Map<String, NamesParser.NumberTitle> parsedCompositionFileNames = parseCompositionFileNames(compositionPaths);
        if (parsedCompositionFileNames != null) {
            Map<String, MediaFileInfo> parsedCompositionMediaInfo = mediaParser.parseCompositions(compositionPaths);
            if (parsedCompositionMediaInfo != null) {
                CompositionsSummary summary = saveCompositionsAndMediaFiles(artifact, compositionPaths, parsedCompositionFileNames, parsedCompositionMediaInfo);

                artifact.setDuration(summary.duration);
                artifact.setSize(summary.size);
                artifact.setInsertDate(LocalDate.now());
                artifactRepository.save(artifact);
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
