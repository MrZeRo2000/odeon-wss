package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.CompositionValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MP3ValidateProcessor extends AbstractFileSystemProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MP3ValidateProcessor.class);

    private final MediaFileRepository mediaFileRepository;

    public MP3ValidateProcessor(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        List<MediaFileValidationDTO> pathValidation = loadFromPath(path);
        List<MediaFileValidationDTO> mediaFileValidation = mediaFileRepository.getMediaFileValidationMusic(ArtifactType.withMP3());

        List<String> artistNames = mediaFileValidation.stream().map(CompositionValidationDTO::getArtifactTitle).distinct().collect(Collectors.toList());

    }

    private List<MediaFileValidationDTO> loadFromPath(Path path) throws ProcessorException {
        List<MediaFileValidationDTO> result = new ArrayList<>();

        try (Stream<Path> artistPathStream = Files.list(path)){
            for (Path artistPath: artistPathStream.collect(Collectors.toList())) {
                logger.debug("Artist path:" + artistPath.getFileName());
                if (!Files.isDirectory(artistPath)) {
                    errorHandler(String.format(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, artistPath.getFileName().toString()));
                } else {
                    loadFromArtistPath(artistPath, result);
                }
            }
        } catch (IOException | ProcessorException e) {
            if (e instanceof ProcessorException) {
                throw (ProcessorException) e;
            } else {
                throw new ProcessorException("Exception:" + e.getMessage());
            }
        }

        return result;
    }

    private void loadFromArtistPath(Path artistPath, List<MediaFileValidationDTO> result)
            throws ProcessorException {
        try (Stream<Path> artifactPathStream = Files.list(artistPath)) {
            for (Path artifactPath: artifactPathStream.collect(Collectors.toList())) {
                logger.debug("Artifact path:" + artifactPath.getFileName());
                if (!Files.isDirectory(artifactPath)) {
                    errorHandler(String.format(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, artistPath.getFileName().toString()));
                } else {
                    loadFromArtifactPath(artistPath, artifactPath, result);
                }
            }
        } catch (IOException e) {
            throw new ProcessorException("Exception:" + e.getMessage());
        }
    }

    private void loadFromArtifactPath(Path artistPath, Path artifactPath, List<MediaFileValidationDTO> result)
            throws ProcessorException {
        NamesParser.YearTitle yt = NamesParser.parseMusicArtifactTitle(artifactPath.getFileName().toString());
        if (yt == null) {
            errorHandler(String.format(ProcessorMessages.ERROR_PARSING_ARTIFACT_NAME, artifactPath.getFileName().toString()));
        } else {
            try (Stream<Path> compositionPathStream = Files.list(artifactPath)) {
                compositionPathStream.forEach(compositionPath -> {
                    String compositionFileName = compositionPath.getFileName().toString();

                    if (Files.isDirectory(compositionPath)) {
                        errorHandler(String.format(ProcessorMessages.ERROR_EXPECTED_FILE,  compositionPath.toAbsolutePath()));
                    } else if (!compositionFileName.endsWith("mp3")) {
                        errorHandler(String.format(ProcessorMessages.ERROR_WRONG_FILE_TYPE, compositionPath.toAbsolutePath()));
                    } else {
                        NamesParser.NumberTitle nt = NamesParser.parseMusicComposition(compositionFileName);
                        if (nt == null) {
                            errorHandler(String.format(ProcessorMessages.ERROR_PARSING_COMPOSITION_NAME, compositionPath.toAbsolutePath().getFileName()));
                        } else {
                            result.add(new MediaFileValidationDTO(
                                    artistPath.getFileName().toString(),
                                    yt.getTitle(),
                                    (long)yt.getYear(),
                                    nt.getNumber(),
                                    nt.getTitle(),
                                    compositionPath.getFileName().toString(),
                                    "mp3"
                            ));
                        }
                    }
                });


            } catch (IOException e) {
                throw new ProcessorException(String.format(ProcessorMessages.ERROR_EXCEPTION, e.getMessage()));
            }
        }
    }
}
