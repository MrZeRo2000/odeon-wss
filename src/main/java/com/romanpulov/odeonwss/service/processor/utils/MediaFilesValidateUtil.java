package com.romanpulov.odeonwss.service.processor.utils;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.service.processor.AbstractProcessor;
import com.romanpulov.odeonwss.service.processor.PathValidator;
import com.romanpulov.odeonwss.service.processor.ProcessorMessages;

import java.util.List;

public class MediaFilesValidateUtil {
    public static void validateMediaFilesMusic(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        processor.processingEventHandler(ProcessorMessages.VALIDATING_MEDIA_FILES);
        if (PathValidator.validateMediaFilesMusic(processor, pathValidation, dbValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
        }
    }
    public static void validateArtifactMediaFilesMusic(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbArtifactValidation) {
        processor.processingEventHandler(ProcessorMessages.VALIDATING_ARTIFACT_MEDIA_FILES);
        if (PathValidator.validateArtifactMediaFilesMusic(processor, pathValidation, dbArtifactValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_VALIDATED);
        }
    }

    public static void validateMediaFileSizeMusic(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbArtifactValidation) {
        processor.processingEventHandler(ProcessorMessages.VALIDATING_MEDIA_FILES_SIZE);
        if (PathValidator.validateMediaFileSizeMusic(processor, pathValidation, dbArtifactValidation)) {
            processor.infoHandler(ProcessorMessages.INFO_MEDIA_FILES_SIZE_MISMATCH_VALIDATED);
        }
    }

    public static void validateMediaFilesAll(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation,
            List<MediaFileValidationDTO> dbArtifactValidation) {
        validateMediaFilesMusic(processor, pathValidation, dbValidation);
        validateArtifactMediaFilesMusic(processor, pathValidation, dbArtifactValidation);
        validateMediaFileSizeMusic(processor, pathValidation, dbValidation);
    }
}
