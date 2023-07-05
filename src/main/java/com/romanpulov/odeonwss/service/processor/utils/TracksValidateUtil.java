package com.romanpulov.odeonwss.service.processor.utils;

import com.romanpulov.odeonwss.dto.ArtifactFlatDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.service.processor.AbstractProcessor;
import com.romanpulov.odeonwss.service.processor.PathValidator;
import com.romanpulov.odeonwss.service.processor.ProcessorMessages;
import com.romanpulov.odeonwss.service.processor.ValueValidator;

import java.util.List;

public class TracksValidateUtil {
    public static void validateMonotonicallyIncreasingTrackNumbers(
            AbstractProcessor processor,
            ArtifactRepository artifactRepository,
            ArtifactType artifactType) {
        List<ArtifactFlatDTO> tns = artifactRepository
                .getArtifactsWithNoMonotonicallyIncreasingTrackNumbers(artifactType.getId());
        if (ValueValidator.validateNonEmptyCollection(
                processor,
                tns,
                ProcessorMessages.ERROR_NO_MONOTONICALLY_INCREASING_TRACK_NUMBERS,
                dto -> dto.getArtistName() == null ?
                        dto.getTitle() :
                        String.format(PathValidator.DELIMITER_FORMAT, dto.getArtistName(), dto.getTitle())
                )) {
            processor.infoHandler(ProcessorMessages.INFO_MONOTONICALLY_INCREASING_TRACK_NUMBER_VALIDATED);
        }
    }
}
