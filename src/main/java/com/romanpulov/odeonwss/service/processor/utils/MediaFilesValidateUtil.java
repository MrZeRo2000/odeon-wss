package com.romanpulov.odeonwss.service.processor.utils;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.service.processor.AbstractProcessor;
import com.romanpulov.odeonwss.service.processor.MediaFileValidator;
import com.romanpulov.odeonwss.service.processor.ProcessorMessages;
import com.romanpulov.odeonwss.service.processor.ValueValidator;

import java.util.List;
import java.util.Optional;

public class MediaFilesValidateUtil {
    public static void validateMediaFilesMusic(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        processor.processingEventHandler(ProcessorMessages.VALIDATING_MEDIA_FILES);
        if (MediaFileValidator.validateMediaFilesMusic(processor, pathValidation, dbValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
        }
    }
    public static void validateArtifactMediaFilesMusic(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbArtifactValidation) {
        processor.processingEventHandler(ProcessorMessages.VALIDATING_ARTIFACT_MEDIA_FILES);
        if (MediaFileValidator.validateArtifactMediaFilesMusic(processor, pathValidation, dbArtifactValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_VALIDATED);
        }
    }

    public static void validateMediaFileSizeMusic(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbArtifactValidation) {
        processor.processingEventHandler(ProcessorMessages.VALIDATING_MEDIA_FILES_SIZE);
        if (MediaFileValidator.validateMediaFileSize(
                processor,
                MediaFileValidator.ARTIFACT_MEDIA_FILE_MUSIC_MAPPER,
                pathValidation,
                dbArtifactValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_MEDIA_FILES_SIZE_MISMATCH_VALIDATED);
        }
    }

    public static void validateMediaFilesMusicAll(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation,
            List<MediaFileValidationDTO> dbArtifactValidation) {
        validateMediaFilesMusic(processor, pathValidation, dbValidation);
        validateArtifactMediaFilesMusic(processor, pathValidation, dbArtifactValidation);
        validateMediaFileSizeMusic(processor, pathValidation, dbValidation);
    }

    public static void validateMediaFilesVideoAll(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation,
            List<MediaFileValidationDTO> dbArtifactValidation) {
        if (MediaFileValidator.validate(
                processor,
                pathValidation,
                dbValidation,
                MediaFileValidator.ARTIFACT_MEDIA_FILE_MAPPER,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_DB)) {
            processor.infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
        }

        if (MediaFileValidator.validateArtifactMediaFiles(processor, pathValidation, dbArtifactValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_VALIDATED);
        }

        if (MediaFileValidator.validateArtifactMediaFileSize(
                processor,
                MediaFileValidator.ARTIFACT_TITLE_MAPPER,
                dbArtifactValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_SIZE_VALIDATED);
        }

        if (ValueValidator.validateConditionValue(
                processor,
                dbArtifactValidation,
                ProcessorMessages.ERROR_MEDIA_FILES_EMPTY_BITRATE,
                m -> Optional.ofNullable(m.getMediaFileBitrate()).orElse(0L).equals(0L),
                m -> MediaFileValidator.DELIMITER_FORMAT.formatted(m.getArtifactTitle(), m.getMediaFileName()))) {
            processor.infoHandler(ProcessorMessages.INFO_MEDIA_FILES_BITRATE_VALIDATED);
        }

        if (MediaFileValidator.validateMediaFileSize(
                processor,
                MediaFileValidator.ARTIFACT_MEDIA_FILE_MAPPER,
                pathValidation,
                dbValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_MEDIA_FILES_SIZE_MISMATCH_VALIDATED);
        }
    }
}
