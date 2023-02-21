package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathValidator {
    public static final String DELIMITER_FORMAT = "%s >> %s";
    private static final String MUSIC_ARTIFACT_ENTITY_FORMAT = "%s >> %d %s >> %s";
    private static final String MUSIC_ARTIFACT_COMPOSITION_FORMAT = "%s >> %d %s >> %02d - %s";
    private static final String EMPTY_STRING_VALUE = "@!EMPTY!@";

    private static String formatNullable(String s) {
        return s == null ? EMPTY_STRING_VALUE : s;
    }

    private interface MediaFileValidationDTOMapper extends Function<MediaFileValidationDTO, String> {
        @Override
        String apply(MediaFileValidationDTO mediaFileValidationDTO);
    }

    private static final MediaFileValidationDTOMapper ARTIST_NAME_MAPPER = MediaFileValidationDTO::getArtistName;
    private static final MediaFileValidationDTOMapper ARTIFACT_TITLE_MAPPER = MediaFileValidationDTO::getArtifactTitle;
    private static final MediaFileValidationDTOMapper MEDIA_FILE_MAPPER = MediaFileValidationDTO::getMediaFileName;

    private static final MediaFileValidationDTOMapper ARTIFACT_MUSIC_MAPPER = m -> String.format(
            DELIMITER_FORMAT,
            m.getArtistName(),
            NamesParser.formatMusicArtifact(m.getArtifactYear(), m.getArtifactTitle()));

    private static final MediaFileValidationDTOMapper ARTIFACT_MEDIA_FILE_MAPPER = m -> String.format(
            DELIMITER_FORMAT,
            m.getArtifactTitle(),
            formatNullable(m.getMediaFileName()));

    private static final MediaFileValidationDTOMapper ARTIFACT_MEDIA_FILE_MUSIC_MAPPER = m -> String.format(
            MUSIC_ARTIFACT_ENTITY_FORMAT,
            m.getArtistName(),
            m.getArtifactYear(),
            m.getArtifactTitle(),
            formatNullable(m.getMediaFileName()));

    private static final MediaFileValidationDTOMapper COMPOSITION_MUSIC_MAPPER = d -> String.format(
            MUSIC_ARTIFACT_COMPOSITION_FORMAT,
            d.getArtistName(),
            d.getArtifactYear(),
            d.getArtifactTitle(),
            d.getCompositionNum(),
            formatNullable(d.getCompositionTitle()));

    private static final MediaFileValidationDTOMapper MEDIA_FILE_MUSIC_MAPPER = d -> String.format(
            MUSIC_ARTIFACT_ENTITY_FORMAT,
            d.getArtistName(),
            d.getArtifactYear(),
            d.getArtifactTitle(),
            formatNullable(d.getMediaFileName()));

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
                .filter(s -> formatNullable(s).contains(EMPTY_STRING_VALUE))
                .collect(Collectors.toSet());

        return ValueValidator.compareStringSets(
                processor,
                pathArtifacts,
                dbArtifacts,
                notInFilesError,
                notInDbError
        );
    }

    public static boolean validateArtistNamesArtifactsCompositions(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        return validate(
                processor,
                pathValidation,
                dbValidation,
                ARTIST_NAME_MAPPER,
                ProcessorMessages.ERROR_ARTISTS_ARTIFACTS_COMPOSITIONS_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTISTS_ARTIFACTS_COMPOSITIONS_NOT_IN_DB
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

    public static boolean validateCompositionsMusic(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        return validate(
                processor,
                pathValidation,
                dbValidation,
                COMPOSITION_MUSIC_MAPPER,
                ProcessorMessages.ERROR_COMPOSITIONS_NOT_IN_FILES,
                ProcessorMessages.ERROR_COMPOSITIONS_NOT_IN_DB
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

    public static boolean validateArtifactMediaFilesMusic(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        return validate(
                processor,
                pathValidation,
                dbValidation,
                ARTIFACT_MEDIA_FILE_MUSIC_MAPPER,
                ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_DB
        );
    }
}
