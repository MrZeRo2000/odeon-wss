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
public class DVMusicValidateProcessor extends AbstractFileSystemProcessor {
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

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        final List<MediaFileValidationDTO> dbValidation = mediaFileRepository.getCompositionMediaFileValidationMusic(
                ArtistType.ARTIST, ArtifactType.withDVMusic()
        );
        final List<MediaFileValidationDTO> pathValidation = PathLoader.loadFromPath(this, path);

        if (validateEmptyMediaFiles()) {
            infoHandler(ProcessorMessages.INFO_MEDIA_FILES_SIZE_VALIDATED);
        }

        if (PathValidator.validateArtifacts(this, pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

            if (PathValidator.validateCompositions(this, pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_COMPOSITIONS_VALIDATED);
            }

            List<MediaFileValidationDTO> dbArtifactMediaFilesValidation = mediaFileRepository.getMediaFileValidationMusic(ArtifactType.withDVMusic());

            if (PathValidator.validateMediaFiles(this, pathValidation, dbArtifactMediaFilesValidation)) {
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
                    mediaFiles.stream().map(MediaFile::getName).collect(Collectors.toList())
            );
            return false;
        }
    }

}
