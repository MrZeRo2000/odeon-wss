package com.romanpulov.odeonwss.service.processor.utils;

import com.romanpulov.odeonwss.dto.ArtifactFlatDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.service.processor.AbstractProcessor;
import com.romanpulov.odeonwss.service.processor.MediaFileValidator;
import com.romanpulov.odeonwss.service.processor.ProcessorMessages;
import com.romanpulov.odeonwss.service.processor.ValueValidator;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;

import java.util.List;

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
}