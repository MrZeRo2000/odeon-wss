package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathValidator {
    private static final String DELIMITER_FORMAT = "%s >> %s";
    private static final String MUSIC_ARTIFACT_ENTITY_FORMAT = "%s >> %d %s >> %s";

    private interface MediaFileValidationDTOMapper extends Function<MediaFileValidationDTO, String> {
        @Override
        String apply(MediaFileValidationDTO mediaFileValidationDTO);
    }

    private static final MediaFileValidationDTOMapper ARTIFACT_TITLE_MAPPER = MediaFileValidationDTO::getArtifactTitle;
    private static final MediaFileValidationDTOMapper MEDIA_FILE_MAPPER = MediaFileValidationDTO::getMediaFileName;

    private static final MediaFileValidationDTOMapper ARTIFACT_MUSIC_MAPPER = m -> String.format(
            DELIMITER_FORMAT,
            m.getArtistName(),
            NamesParser.formatMusicArtifact(m.getArtifactYear(), m.getArtifactTitle()));

    private static final MediaFileValidationDTOMapper ARTIFACT_MEDIA_FILE_MAPPER = m -> String.format(
            DELIMITER_FORMAT,
            m.getArtifactTitle(),
            m.getMediaFileName());

    private static final MediaFileValidationDTOMapper MEDIA_FILE_MUSIC_MAPPER = d -> String.format(
            MUSIC_ARTIFACT_ENTITY_FORMAT,
            d.getArtistName(),
            d.getArtifactYear(),
            d.getArtifactTitle(),
            d.getMediaFileName());

    public static boolean validate(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation,
            MediaFileValidationDTOMapper mapper,
            String notInFilesError,
            String notInDbError
    ) {
        Set<String> pathArtifacts = pathValidation
                .stream()
                .map(mapper)
                .collect(Collectors.toSet());
        Set<String> dbArtifacts = dbValidation
                .stream()
                .map(mapper)
                .collect(Collectors.toSet());

        return ValueValidator.compareStringSets(
                processor,
                pathArtifacts,
                dbArtifacts,
                notInFilesError,
                notInDbError
        );
    }

    public static boolean validateArtifacts(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        return validate(
                processor,
                pathValidation,
                dbValidation,
                ARTIFACT_TITLE_MAPPER,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_DB
        );
    }

    public static boolean validateArtifactsMusic(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        return validate(
                processor,
                pathValidation,
                dbValidation,
                ARTIFACT_MUSIC_MAPPER,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_DB
        );
    }

    public static boolean validateMediaFiles(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        return validate(
                processor,
                pathValidation,
                dbValidation,
                ARTIFACT_MEDIA_FILE_MAPPER,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_DB
        );
    }

    public static boolean validateMediaFilesMusic(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        return validate(
                processor,
                pathValidation,
                dbValidation,
                MEDIA_FILE_MUSIC_MAPPER,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_DB
        );
    }

    public static boolean validateArtifactMediaFiles(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        return validate(
                processor,
                pathValidation,
                dbValidation,
                MEDIA_FILE_MAPPER,
                ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_DB
        );
    }
}
