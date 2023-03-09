package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class DVMoviesValidateProcessor extends AbstractFileSystemProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DVMoviesValidateProcessor.class);
    public static final ArtifactType ARTIFACT_TYPE = ArtifactType.withDVMovies();

    private final MediaFileRepository mediaFileRepository;

    public DVMoviesValidateProcessor(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        List<MediaFileValidationDTO> dbValidation = mediaFileRepository.getTrackMediaFileValidationDV(
                ARTIFACT_TYPE);
        logger.info("dbValidation:" + dbValidation);

        final List<MediaFileValidationDTO> pathValidation = PathValidationLoader.loadFromPath(this, path);
        logger.info("pathValidation:" + pathValidation);

        if (PathValidator.validateArtifacts(this, pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

            if (PathValidator.validateMediaFiles(this, pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
            }

            List<MediaFileValidationDTO> dbArtifactValidation = mediaFileRepository
                    .getArtifactMediaFileValidationDV(ARTIFACT_TYPE);

            if (PathValidator.validateArtifactMediaFiles(this, pathValidation, dbArtifactValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_VALIDATED);
            }
        }
    }
}
