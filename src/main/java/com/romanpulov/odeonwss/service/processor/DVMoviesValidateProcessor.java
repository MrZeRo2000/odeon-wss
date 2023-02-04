package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.CompositionValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DVMoviesValidateProcessor extends AbstractFileSystemProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DVMoviesValidateProcessor.class);

    private final MediaFileRepository mediaFileRepository;

    public DVMoviesValidateProcessor(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        List<MediaFileValidationDTO> dbValidation = mediaFileRepository.getCompositionMediaFileValidationDV(
                ArtifactType.withDVMovies());
        logger.info("dbValidation:" + dbValidation);

        final List<MediaFileValidationDTO> pathValidation = PathLoader.loadFromPath(this, path);
        logger.info("pathValidation:" + pathValidation);

        if (PathValidator.validateArtifacts(this, pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

            if (PathValidator.validateMediaFiles(this, pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
            }

            dbValidation = mediaFileRepository.getArtifactMediaFileValidationDV(
                    ArtifactType.withDVMovies());

            if (validateArtifactMediaFiles(pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_VALIDATED);
            }
        }
    }

    protected boolean validateArtifactMediaFiles(List<MediaFileValidationDTO> pathValidation, List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathMediaFiles = pathValidation.stream()
                .map(MediaFileValidationDTO::getMediaFileName)
                .collect(Collectors.toSet());
        Set<String> dbMediaFiles = dbValidation.stream()
                .map(MediaFileValidationDTO::getMediaFileName)
                .collect(Collectors.toSet());

        return ValueValidator.compareStringSets(
                this,
                pathMediaFiles,
                dbMediaFiles,
                ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_DB
        );
    }

}
