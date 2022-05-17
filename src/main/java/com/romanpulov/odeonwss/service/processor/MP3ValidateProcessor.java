package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.CompositionValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTOBuilder;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
        List<MediaFileValidationDTO> dbValidation = mediaFileRepository.getMediaFileValidationMusic(ArtifactType.withMP3());

        if (validateArtistNames(pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTISTS_VALIDATED);

            if (validateArtifacts(pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

                if (validateCompositions(pathValidation, dbValidation)) {
                    infoHandler(ProcessorMessages.INFO_COMPOSITIONS_VALIDATED);
                }
            }
        }
    }

    private List<MediaFileValidationDTO> loadFromPath(Path path) throws ProcessorException {
        List<MediaFileValidationDTO> result = new ArrayList<>();

        try (Stream<Path> artistPathStream = Files.list(path)){
            for (Path artistPath: artistPathStream.collect(Collectors.toList())) {
                logger.debug("Artist path:" + artistPath.getFileName());
                if (!Files.isDirectory(artistPath)) {
                    errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, artistPath.getFileName().toString());
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
                    errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, artistPath.getFileName().toString());
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
            errorHandler(ProcessorMessages.ERROR_PARSING_ARTIFACT_NAME, artifactPath.getFileName().toString());
            result.add(
                    new MediaFileValidationDTOBuilder()
                            .withArtistName(artistPath.getFileName().toString())
                            .build()
            );
        } else {
            try (Stream<Path> compositionPathStream = Files.list(artifactPath)) {
                compositionPathStream.forEach(compositionPath -> {
                    String compositionFileName = compositionPath.getFileName().toString();

                    if (Files.isDirectory(compositionPath)) {
                        errorHandler(ProcessorMessages.ERROR_EXPECTED_FILE,  compositionPath.toAbsolutePath());
                    } else if (!compositionFileName.endsWith("mp3")) {
                        errorHandler(ProcessorMessages.ERROR_WRONG_FILE_TYPE, compositionPath.toAbsolutePath());
                    } else {
                        NamesParser.NumberTitle nt = NamesParser.parseMusicComposition(compositionFileName);
                        if (nt == null) {
                            errorHandler(ProcessorMessages.ERROR_PARSING_COMPOSITION_NAME, compositionPath.toAbsolutePath().getFileName());
                            result.add(
                              new MediaFileValidationDTOBuilder()
                                      .withArtistName(artistPath.getFileName().toString())
                                      .withArtifactTitle(yt.getTitle())
                                      .withArtifactYear(yt.getYear())
                                      .build()
                            );
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
                throw new ProcessorException(ProcessorMessages.ERROR_EXCEPTION, e.getMessage());
            }
        }
    }

    private boolean compareStringSets(
            Set<String> pathStrings,
            Set<String> dbStrings,
            String pathErrorMessage,
            String dbErrorMessage)
    {
        boolean result = true;

        Set<String> pathStringsDiff = new HashSet<>(pathStrings);
        pathStringsDiff.removeAll(dbStrings);
        if (!pathStringsDiff.isEmpty()) {
            errorHandler(dbErrorMessage, String.join(",", pathStringsDiff));
            result = false;
        }

        Set<String> dbStringsDiff = new HashSet<>(dbStrings);
        dbStringsDiff.removeAll(pathStrings);
        if (!dbStringsDiff.isEmpty()) {
            errorHandler(pathErrorMessage, String.join(",", dbStringsDiff));
            result = false;
        }

        return result;

    }

    private boolean validateArtistNames(List<MediaFileValidationDTO> pathValidation, List<MediaFileValidationDTO> dbValidation) {

        Set<String> pathArtistNames = pathValidation.stream().map(CompositionValidationDTO::getArtistName).collect(Collectors.toSet());
        Set<String> dbArtistNames = dbValidation.stream().map(CompositionValidationDTO::getArtistName).collect(Collectors.toSet());

        return compareStringSets(
                pathArtistNames,
                dbArtistNames,
                ProcessorMessages.ERROR_ARTISTS_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTISTS_NOT_IN_DB
        );
    }

    private boolean validateArtifacts(List<MediaFileValidationDTO> pathValidation, List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathArtifacts = pathValidation.stream()
                .map(d -> d.getArtistName() +
                        ProcessorMessages.FORMAT_PATH_DELIMITER +
                        NamesParser.formatMusicArtifact(d.getArtifactYear(), d.getArtifactTitle()))
                .collect(Collectors.toSet());
        Set<String> dbArtifacts = dbValidation.stream()
                .map(d -> d.getArtistName() +
                        ProcessorMessages.FORMAT_PATH_DELIMITER +
                        NamesParser.formatMusicArtifact(d.getArtifactYear(), d.getArtifactTitle()))
                .collect(Collectors.toSet());

        return compareStringSets(
                pathArtifacts,
                dbArtifacts,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_DB
        );
    }

    private boolean validateCompositions(List<MediaFileValidationDTO> pathValidation, List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathCompositions = pathValidation.stream()
                .map(d ->
                        d.getArtistName() +
                        ProcessorMessages.FORMAT_PATH_DELIMITER +
                        NamesParser.formatMusicArtifact(d.getArtifactYear(), d.getArtifactTitle()) +
                        ProcessorMessages.FORMAT_PATH_DELIMITER +
                        NamesParser.formatMusicCompositionWithFile(d.getCompositionNum(), d.getCompositionTitle(), d.getMediaFileName()))
                .collect(Collectors.toSet());
        Set<String> dbCompositions = dbValidation.stream()
                .map(d ->
                        d.getArtistName() +
                        ProcessorMessages.FORMAT_PATH_DELIMITER +
                        NamesParser.formatMusicArtifact(d.getArtifactYear(), d.getArtifactTitle()) +
                        ProcessorMessages.FORMAT_PATH_DELIMITER +
                        NamesParser.formatMusicCompositionWithFile(d.getCompositionNum(), d.getCompositionTitle(), d.getMediaFileName()))
                .collect(Collectors.toSet());

        return compareStringSets(
                pathCompositions,
                dbCompositions,
                ProcessorMessages.ERROR_COMPOSITIONS_NOT_IN_FILES,
                ProcessorMessages.ERROR_COMPOSITIONS_NOT_IN_DB
        );
    }

}
