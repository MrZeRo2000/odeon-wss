package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.dto.MediaFileEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.MediaFormatInfo;

public class MediaFileMapper {
    public static MediaFile fromCompositionEditDTO(CompositionEditDTO editDTO) {
        MediaFile mediaFile = new MediaFile();

        Artifact artifact = new Artifact();
        artifact.setId(editDTO.getArtifactId());
        mediaFile.setArtifact(artifact);

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

    public static MediaFile fromMediaFileEditDTO(MediaFileEditDTO editDTO) {
        MediaFile mediaFile = new MediaFile();

        mediaFile.setId(editDTO.getId());

        Artifact artifact = new Artifact();
        artifact.setId(editDTO.getArtifactId());

        mediaFile.setArtifact(artifact);
        mediaFile.setName(editDTO.getName());
        mediaFile.setFormat(editDTO.getFormat());
        mediaFile.setSize(editDTO.getSize());
        mediaFile.setBitrate(editDTO.getBitrate());
        mediaFile.setDuration(editDTO.getDuration());

        return mediaFile;
    }

}
