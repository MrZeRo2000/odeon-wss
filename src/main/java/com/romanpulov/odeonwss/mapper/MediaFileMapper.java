package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.MediaFile;

public class MediaFileMapper {
    public static MediaFile fromCompositionEditDTO(CompositionEditDTO editDTO, Composition composition) {
        MediaFile mediaFile = new MediaFile();

        mediaFile.setComposition(composition);
        mediaFile.setName(editDTO.getMediaName());
        mediaFile.setFormat(editDTO.getMediaFormat());
        mediaFile.setSize(editDTO.getMediaSize());
        mediaFile.setBitrate(editDTO.getMediaBitrate());
        mediaFile.setDuration(editDTO.getMediaDuration());

        return mediaFile;
    }

    public static MediaFile fromCompositionEditDTO(CompositionEditDTO editDTO, MediaFile mediaFile, Composition composition) {
        MediaFile updatedMediaFile = MediaFileMapper.fromCompositionEditDTO(editDTO, composition);
        updatedMediaFile.setId(mediaFile.getId());

        return updatedMediaFile;
    }

}
