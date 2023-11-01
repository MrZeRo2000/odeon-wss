package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.TrackFlatDTO;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrackValidator {
    public static boolean validateArtifactTrackDuration(
            AbstractProcessor processor,
            List<TrackFlatDTO> tracks) {
        Map<String, Long> artifactDurations = tracks
                .stream()
                .map(v -> Pair.of(
                        v.getArtifactTitle(),
                        Optional.ofNullable(v.getArtifactDuration()).orElse(0L)))
                .distinct()
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

        Map<String, Long> artifactTrackDurations = tracks
                .stream()
                .collect(Collectors.groupingBy(
                        TrackFlatDTO::getArtifactTitle,
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
