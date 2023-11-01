package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.TrackFlatDTO;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TrackValidator {
    public static final String DELIMITER_FORMAT = "%s >> %s";

    public static final Function<TrackFlatDTO, String> ARTIFACT_TITLE_MAPPER = TrackFlatDTO::getArtifactTitle;

    public static final Function<TrackFlatDTO, String> ARTIFACT_MUSIC_MAPPER = t -> String.format(
            DELIMITER_FORMAT,
            t.getArtifactArtistName(),
            NamesParser.formatMusicArtifact(t.getArtifactYear(), t.getArtifactTitle()));

    public static boolean validateArtifactTrackDuration(
            AbstractProcessor processor,
            Function<TrackFlatDTO, String> mapper,
            List<TrackFlatDTO> tracks) {
        Map<String, Long> artifactDurations = tracks
                .stream()
                .map(v -> Pair.of(
                        mapper.apply(v),
                        Optional.ofNullable(v.getArtifactDuration()).orElse(0L)))
                .distinct()
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

        Map<String, Long> artifactTrackDurations = tracks
                .stream()
                .collect(Collectors.groupingBy(
                        mapper,
                        Collectors.summingLong(v -> Optional.ofNullable(v.getDuration()).orElse(0L))));

        List<String> mismatchArtifactTitles = artifactDurations
                .entrySet()
                .stream()
                .filter(a -> !artifactTrackDurations.getOrDefault(a.getKey(), 0L).equals(a.getValue()))
                .map(Map.Entry::getKey)
                .sorted()
                .toList();

        if (mismatchArtifactTitles.isEmpty()) {
            return true;
        } else {
            processor.errorHandler(ProcessorMessages.ERROR_ARTIFACT_TRACKS_DURATION_MISMATCH, mismatchArtifactTitles);
            return false;
        }
    }
}
