package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.CompositionValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DVMusicValidateProcessor extends AbstractValidateProcessor {
    private static final String FORMAT_DELIMITER = " >> ";

    private final MediaFileRepository mediaFileRepository;

    private static class MediaFileValidation {
        private final String artifactTitle;
        private final String mediaFileName;

        public MediaFileValidation(String artifactTitle, String mediaFileName) {
            this.artifactTitle = artifactTitle;
            this.mediaFileName = mediaFileName;
        }
    }

    public DVMusicValidateProcessor(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    private List<MediaFileValidation> loadFromPath(Path path) throws ProcessorException {
        List<MediaFileValidation> result = new ArrayList<>();

        try (Stream<Path> artifactPathStream = Files.list(path)) {
            for (Path artifactPath: artifactPathStream.collect(Collectors.toList())) {
                if (!Files.isDirectory(artifactPath)) {
                    errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, artifactPath.getFileName().toString());
                } else {
                    try (Stream<Path> mediaFileStream = Files.list(artifactPath)) {
                        for (Path mediaFilePath: mediaFileStream.collect(Collectors.toList())) {
                            if (Files.isDirectory(mediaFilePath)) {
                                errorHandler(ProcessorMessages.ERROR_EXPECTED_FILE, mediaFilePath.getFileName().toString());
                            } else {
                                result.add(new MediaFileValidation(
                                        artifactPath.getFileName().toString(),
                                        mediaFilePath.getFileName().toString())
                                );
                            }
                        }

                    } catch (IOException e) {
                        throw new ProcessorException("Exception:" + e.getMessage());
                    }
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

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        List<MediaFileValidationDTO> dbValidation = mediaFileRepository.getCompositionMediaFileValidationMusic(
                ArtistType.ARTIST, ArtifactType.withDVMusic()
        );
        List<MediaFileValidation> pathValidation = loadFromPath(path);

        if (validateEmptyMediaFiles()) {
            infoHandler(ProcessorMessages.INFO_MEDIA_FILES_SIZE_VALIDATED);
        }

        if (validateArtifacts(pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

            if (validateCompositions(pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_COMPOSITIONS_VALIDATED);
            }

            List<MediaFileValidationDTO> dbArtifactMediaFilesValidation = mediaFileRepository.getMediaFileValidationMusic(ArtifactType.withDVMusic());

            if (validateMediaFiles(pathValidation, dbArtifactMediaFilesValidation)) {
                infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
            }
        }
    }

    private boolean validateEmptyMediaFiles() {
        List<MediaFile> mediaFiles = mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(ArtifactType.withDVMusic());
        if (mediaFiles.isEmpty()) {
            return true;
        } else {
            errorHandler(
                    ProcessorMessages.ERROR_MEDIA_FILES_EMPTY_SIZE,
                    String.join(",", mediaFiles.stream().map(MediaFile::getName).collect(Collectors.toSet()))
            );
            return false;
        }
    }

    private boolean validateArtifacts(List<MediaFileValidation> pathValidation, List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathArtifacts = pathValidation.stream().map(d -> d.artifactTitle).collect(Collectors.toSet());
        Set<String> dbArtifacts = dbValidation.stream().map(CompositionValidationDTO::getArtifactTitle).collect(Collectors.toSet());

        return compareStringSets(
                pathArtifacts,
                dbArtifacts,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_DB
        );
    }

    private boolean validateCompositions(List<MediaFileValidation> pathValidation, List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathCompositions = pathValidation.stream().map(d -> d.artifactTitle + FORMAT_DELIMITER + d.mediaFileName).collect(Collectors.toSet());
        Set<String> dbCompositions = dbValidation.stream().map(d -> d.getArtifactTitle() + FORMAT_DELIMITER + d.getMediaFileName()).collect(Collectors.toSet());

        return compareStringSets(
                pathCompositions,
                dbCompositions,
                ProcessorMessages.ERROR_COMPOSITIONS_NOT_IN_FILES,
                ProcessorMessages.ERROR_COMPOSITIONS_NOT_IN_DB
        );
    }

    private boolean validateMediaFiles(List<MediaFileValidation> pathValidation, List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathCompositions = pathValidation.stream().map(d -> d.artifactTitle + FORMAT_DELIMITER + d.mediaFileName).collect(Collectors.toSet());
        Set<String> dbCompositions = dbValidation.stream().map(d -> d.getArtifactTitle() + FORMAT_DELIMITER + d.getMediaFileName()).collect(Collectors.toSet());

        return compareStringSets(
                pathCompositions,
                dbCompositions,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_DB
        );
    }
}
