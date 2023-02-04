package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.CompositionValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PathValidator {
    private static final String ARTIFACT_ENTITY_FORMAT = "%s >> %s";

    public static boolean validateArtifacts(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathArtifacts = pathValidation
                .stream()
                .map(CompositionValidationDTO::getArtifactTitle)
                .collect(Collectors.toSet());
        Set<String> dbArtifacts = dbValidation
                .stream()
                .map(CompositionValidationDTO::getArtifactTitle)
                .collect(Collectors.toSet());

        return ValueValidator.compareStringSets(
                processor,
                pathArtifacts,
                dbArtifacts,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_DB
        );
    }

    public static boolean validateMediaFiles(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathMediaFiles = pathValidation.stream()
                .map(m -> String.format(ARTIFACT_ENTITY_FORMAT, m.getArtifactTitle(), m.getMediaFileName()))
                .collect(Collectors.toSet());
        Set<String> dbMediaFiles = dbValidation.stream()
                .map(m -> String.format(ARTIFACT_ENTITY_FORMAT, m.getArtifactTitle(), m.getMediaFileName()))
                .collect(Collectors.toSet());

        return ValueValidator.compareStringSets(
                processor,
                pathMediaFiles,
                dbMediaFiles,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_DB
        );
    }

    public static boolean validateCompositions(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathCompositions = pathValidation
                .stream()
                .map(d -> String.format(ARTIFACT_ENTITY_FORMAT, d.getArtifactTitle(), d.getCompositionTitle()))
                .collect(Collectors.toSet());
        Set<String> dbCompositions = dbValidation
                .stream()
                .map(d -> String.format(ARTIFACT_ENTITY_FORMAT, d.getArtifactTitle(), d.getCompositionTitle()))
                .collect(Collectors.toSet());

        return ValueValidator.compareStringSets(
                processor,
                pathCompositions,
                dbCompositions,
                ProcessorMessages.ERROR_COMPOSITIONS_NOT_IN_FILES,
                ProcessorMessages.ERROR_COMPOSITIONS_NOT_IN_DB
        );
    }

    public static boolean validateArtifactMediaFiles(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathMediaFiles = pathValidation.stream()
                .map(MediaFileValidationDTO::getMediaFileName)
                .collect(Collectors.toSet());
        Set<String> dbMediaFiles = dbValidation.stream()
                .map(MediaFileValidationDTO::getMediaFileName)
                .collect(Collectors.toSet());

        return ValueValidator.compareStringSets(
                processor,
                pathMediaFiles,
                dbMediaFiles,
                ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_DB
        );
    }

}
