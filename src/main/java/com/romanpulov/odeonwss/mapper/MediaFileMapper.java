package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.MediaFormatInfo;

public class MediaFileMapper {
    public static MediaFile fromCompositionEditDTO(CompositionEditDTO editDTO) {
        MediaFile mediaFile = new MediaFile();

        Artifact artifact = new Artifact();
        artifact.setId(editDTO.getArtifactId());

        mediaFile.setArtifact(artifact);
        mediaFile.setName(editDTO.getMediaName());
        mediaFile.setFormat(editDTO.getMediaFormat());
        mediaFile.setSize(editDTO.getMediaSize());
        mediaFile.setBitrate(editDTO.getMediaBitrate());
        mediaFile.setDuration(editDTO.getMediaDuration());

        return mediaFile;
    }

    public static MediaFile fromCompositionEditDTO(CompositionEditDTO editDTO, MediaFile mediaFile) {
        MediaFile updatedMediaFile = MediaFileMapper.fromCompositionEditDTO(editDTO);
        updatedMediaFile.setId(mediaFile.getId());

        return updatedMediaFile;
    }

    public static MediaFile fromMediaFileInfo(MediaFileInfo mediaFileInfo) {
        MediaFormatInfo mediaFormatInfo = mediaFileInfo.getMediaContentInfo().getMediaFormatInfo();

        MediaFile mediaFile = new MediaFile();

        mediaFile.setName(mediaFileInfo.getFileName());
        mediaFile.setFormat(mediaFormatInfo.getFormatName());
        mediaFile.setSize(mediaFormatInfo.getSize());
        mediaFile.setBitrate(mediaFormatInfo.getBitRate());
        mediaFile.setDuration(mediaFormatInfo.getDuration());

        return mediaFile;
    }

}
