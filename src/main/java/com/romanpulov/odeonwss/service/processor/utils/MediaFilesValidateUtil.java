package com.romanpulov.odeonwss.service.processor.utils;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.service.processor.AbstractProcessor;
import com.romanpulov.odeonwss.service.processor.MediaFileValidator;
import com.romanpulov.odeonwss.service.processor.ProcessorMessages;
import com.romanpulov.odeonwss.service.processor.ValueValidator;

import java.util.List;
import java.util.Optional;

public class MediaFilesValidateUtil {

    public static void validateMediaFilesMusicAll(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation,
            List<MediaFileValidationDTO> dbArtifactValidation) {
        processor.processingEventHandler(ProcessorMessages.VALIDATING_MEDIA_FILES);
        if (MediaFileValidator.validate(
                processor,
                pathValidation,
                dbValidation,
                MediaFileValidator.MEDIA_FILE_MUSIC_MAPPER,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_DB)) {
            processor.infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
        }

        processor.processingEventHandler(ProcessorMessages.VALIDATING_ARTIFACT_MEDIA_FILES);
        if (MediaFileValidator.validate(
                processor,
                pathValidation,
                dbArtifactValidation,
                MediaFileValidator.ARTIFACT_MEDIA_FILE_MUSIC_MAPPER,
                ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_DB)) {
            processor.infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_VALIDATED);
        }

        if (MediaFileValidator.validateArtifactMediaFileSize(
                processor,
                MediaFileValidator.ARTIFACT_MUSIC_MAPPER,
                dbArtifactValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_SIZE_VALIDATED);
        }

        if (MediaFileValidator.validateArtifactMediaFileDuration(
                processor,
                MediaFileValidator.ARTIFACT_MUSIC_MAPPER,
                dbArtifactValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_DURATION_VALIDATED);
        }

        processor.processingEventHandler(ProcessorMessages.VALIDATING_MEDIA_FILES_SIZE);
        if (MediaFileValidator.validateMediaFileSize(
                processor,
                MediaFileValidator.ARTIFACT_MEDIA_FILE_MUSIC_MAPPER,
                pathValidation,
                dbValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_MEDIA_FILES_SIZE_MISMATCH_VALIDATED);
        }
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

        if (MediaFileValidator.validate(
                processor,
                pathValidation,
                dbArtifactValidation,
                MediaFileValidator.MEDIA_FILE_MAPPER,
                ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_DB)) {
            processor.infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_VALIDATED);
        }

        if (MediaFileValidator.validateArtifactMediaFileSize(
                processor,
                MediaFileValidator.ARTIFACT_TITLE_MAPPER,
                dbArtifactValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_SIZE_VALIDATED);
        }

        if (MediaFileValidator.validateArtifactMediaFileDuration(
                processor,
                MediaFileValidator.ARTIFACT_TITLE_MAPPER,
                dbArtifactValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_DURATION_VALIDATED);
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
