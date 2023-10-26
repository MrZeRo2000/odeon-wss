package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MediaFileValidator {
    public static final String DELIMITER_FORMAT = "%s >> %s";
    private static final String MUSIC_ARTIFACT_ENTITY_FORMAT = "%s >> %d %s >> %s";
    private static final String MUSIC_ARTIFACT_TRACK_FORMAT = "%s >> %d %s >> %02d - %s";
    private static final String EMPTY_STRING_VALUE = "@!EMPTY!@";

    private static String formatNullable(String s) {
        return s == null ? EMPTY_STRING_VALUE : s;
    }

    public interface MediaFileValidationDTOMapper extends Function<MediaFileValidationDTO, String> {
        @Override
        String apply(MediaFileValidationDTO mediaFileValidationDTO);
    }

    public static final MediaFileValidationDTOMapper ARTIST_NAME_MAPPER = MediaFileValidationDTO::getArtistName;
    public static final MediaFileValidationDTOMapper ARTIFACT_TITLE_MAPPER = MediaFileValidationDTO::getArtifactTitle;
    public static final MediaFileValidationDTOMapper MEDIA_FILE_MAPPER = MediaFileValidationDTO::getMediaFileName;

    public static final MediaFileValidationDTOMapper ARTIFACT_MUSIC_MAPPER = m -> String.format(
            DELIMITER_FORMAT,
            m.getArtistName(),
            NamesParser.formatMusicArtifact(m.getArtifactYear(), m.getArtifactTitle()));

    public static final MediaFileValidationDTOMapper ARTIFACT_MEDIA_FILE_MAPPER = m -> String.format(
            DELIMITER_FORMAT,
            m.getArtifactTitle(),
            formatNullable(m.getMediaFileName()));

    public static final MediaFileValidationDTOMapper ARTIFACT_MEDIA_FILE_MUSIC_MAPPER = m -> String.format(
            MUSIC_ARTIFACT_ENTITY_FORMAT,
            m.getArtistName(),
            m.getArtifactYear(),
            m.getArtifactTitle(),
            formatNullable(m.getMediaFileName()));

    public static final MediaFileValidationDTOMapper TRACK_MUSIC_MAPPER = d -> String.format(
            MUSIC_ARTIFACT_TRACK_FORMAT,
            d.getArtistName(),
            d.getArtifactYear(),
            d.getArtifactTitle(),
            d.getTrackNum(),
            formatNullable(d.getTrackTitle()));

    public static final MediaFileValidationDTOMapper MEDIA_FILE_MUSIC_MAPPER = d -> String.format(
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
                .filter(s -> !Objects.isNull(s) && !s.contains(EMPTY_STRING_VALUE))
                .collect(Collectors.toSet());

        return ValueValidator.compareStringSets(
                processor,
                pathArtifacts,
                dbArtifacts,
                notInFilesError,
                notInDbError
        );
    }

    public static boolean validateArtistNamesArtifactsTracks(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        return validate(
                processor,
                pathValidation,
                dbValidation,
                ARTIST_NAME_MAPPER,
                ProcessorMessages.ERROR_ARTISTS_ARTIFACTS_TRACKS_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTISTS_ARTIFACTS_TRACKS_NOT_IN_DB
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

    public static boolean validateTracksMusic(
            AbstractProcessor processor,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        return validate(
                processor,
                pathValidation,
                dbValidation,
                TRACK_MUSIC_MAPPER,
                ProcessorMessages.ERROR_TRACKS_NOT_IN_FILES,
                ProcessorMessages.ERROR_TRACKS_NOT_IN_DB
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

    public static boolean validateMediaFileSize (
            AbstractProcessor processor,
            MediaFileValidationDTOMapper mapper,
            List<MediaFileValidationDTO> pathValidation,
            List<MediaFileValidationDTO> dbValidation) {
        Map<String, Long> pathSizes = pathValidation
                .stream()
                .collect(Collectors.toMap(
                        mapper,
                        p -> Optional.ofNullable(p.getMediaFileSize()).orElse(0L)));

        Map<String, Long> dbSizes = dbValidation
                .stream()
                .map(v -> Pair.of(
                        mapper.apply(v),
                        Optional.ofNullable(v.getMediaFileSize()).orElse(0L)))
                .distinct()
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

        List<String> mismatchPaths = pathSizes
                .entrySet()
                .stream()
                .filter(e -> !dbSizes.getOrDefault(e.getKey(), 0L).equals(e.getValue()))
                .map(Map.Entry::getKey)
                .sorted()
                .toList();

        if (mismatchPaths.isEmpty()) {
            return true;
        } else {
            processor.errorHandler(ProcessorMessages.ERROR_MEDIA_FILES_SIZE_MISMATCH, mismatchPaths);
            return false;
        }
    }

    public static boolean validateArtifactMediaFileSize(
            AbstractProcessor processor,
            MediaFileValidationDTOMapper mapper,
            List<MediaFileValidationDTO> dbArtifactValidation) {
        Map<String, Long> artifactSizes = dbArtifactValidation
                .stream()
                .map(v -> Pair.of(
                        mapper.apply(v),
                        Optional.ofNullable(v.getArtifactSize()).orElse(0L)))
                .distinct()
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

        Map<String, Long> artifactMediaFileSizes = dbArtifactValidation
                .stream()
                .collect(Collectors.groupingBy(
                        mapper,
                        Collectors.summingLong(v -> Optional.ofNullable(v.getMediaFileSize()).orElse(0L))));

        List<String> mismatchArtifactTitles = artifactSizes
                .entrySet()
                .stream()
                .filter(a -> !artifactMediaFileSizes.getOrDefault(a.getKey(), 0L).equals(a.getValue()))
                .map(Map.Entry::getKey)
                .sorted()
                .toList();

        if (mismatchArtifactTitles.isEmpty()) {
            return true;
        } else {
            processor.errorHandler(ProcessorMessages.ERROR_ARTIFACT_MEDIA_FILES_SIZE_MISMATCH, mismatchArtifactTitles);
            return false;
        }
    }

}
