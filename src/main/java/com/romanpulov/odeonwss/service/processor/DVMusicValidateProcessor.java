package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DVMusicValidateProcessor extends AbstractFileSystemProcessor {
    private static final ArtifactType ARTIFACT_TYPE = ArtifactType.withDVMusic();

    private final MediaFileRepository mediaFileRepository;

    public DVMusicValidateProcessor(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        final List<MediaFileValidationDTO> dbValidation = mediaFileRepository.getCompositionMediaFileValidationMusic(
                ArtistType.ARTIST, ARTIFACT_TYPE
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

            List<MediaFileValidationDTO> dbArtifactMediaFilesValidation = mediaFileRepository
                    .getMediaFileValidationMusic(ARTIFACT_TYPE);

            if (PathValidator.validateMediaFiles(this, pathValidation, dbArtifactMediaFilesValidation)) {
                infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
            }

            List<MediaFileValidationDTO> dbArtifactValidation = mediaFileRepository
                    .getArtifactMediaFileValidationDV(ARTIFACT_TYPE);

            if (PathValidator.validateArtifactMediaFiles(this, pathValidation, dbArtifactValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_VALIDATED);
            }
        }
    }

    private boolean validateEmptyMediaFiles() {
        List<MediaFile> mediaFiles = mediaFileRepository
                .getMediaFilesWithEmptySizeByArtifactType(ARTIFACT_TYPE);
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
