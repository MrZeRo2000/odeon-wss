package com.romanpulov.odeonwss.service.processor.utils;

import com.romanpulov.odeonwss.dto.ArtifactFlatDTO;
import com.romanpulov.odeonwss.dto.TrackFlatDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.service.processor.*;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;

import java.util.List;
import java.util.function.Function;

public class TracksValidateUtil {
    public static void validateMonotonicallyIncreasingTrackNumbers(
            AbstractProcessor processor,
            ArtifactRepository artifactRepository,
            List<ArtistType> artistTypes,
            List<ArtifactType> artifactTypes) {
        List<ArtifactFlatDTO> tns = artifactRepository
                .getArtifactsWithNoMonotonicallyIncreasingTrackNumbers(
                        artistTypes == null ? null : artistTypes.stream().map(ArtistType::getCode).toList(),
                        artifactTypes.stream().map(ArtifactType::getId).toList());
        if (ValueValidator.validateNonEmptyCollection(
                processor,
                tns,
                ProcessorMessages.ERROR_NO_MONOTONICALLY_INCREASING_TRACK_NUMBERS,
                dto -> dto.getArtistName() == null ?
                        dto.getTitle() :
                        String.format(
                                MediaFileValidator.DELIMITER_FORMAT,
                                dto.getArtistName(),
                                dto.getYear() == null ?
                                        dto.getTitle() :
                                        String.format(NamesParser.formatMusicArtifact(dto.getYear(), dto.getTitle()))
                        )
                )) {
            processor.infoHandler(ProcessorMessages.INFO_MONOTONICALLY_INCREASING_TRACK_NUMBER_VALIDATED);
        }
    }

    public static void validateTracksDuration (
            AbstractProcessor processor,
            Function<TrackFlatDTO, String> mapper,
            List<TrackFlatDTO> tracks) {
        if (TrackValidator.validateArtifactTrackDuration(
                processor,
                mapper,
                tracks)) {
            processor.infoHandler(ProcessorMessages.INFO_ARTIFACT_TRACKS_DURATION_VALIDATED);
        }
    }
}