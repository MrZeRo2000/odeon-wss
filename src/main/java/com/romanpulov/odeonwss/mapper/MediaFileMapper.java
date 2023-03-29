package com.romanpulov.odeonwss.mapper;

import com.romanpulov.jutilscore.io.FileUtils;
import com.romanpulov.odeonwss.dto.MediaFileEditDTO;
import com.romanpulov.odeonwss.dto.TrackEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.MediaFormatInfo;
import org.springframework.stereotype.Component;

@Component
public class MediaFileMapper {
    public MediaFile fromTrackEditDTO(TrackEditDTO editDTO) {
        MediaFile mediaFile = new MediaFile();

        Artifact artifact = new Artifact();
        artifact.setId(editDTO.getArtifactId());
        mediaFile.setArtifact(artifact);

        return mediaFile;
    }

    public MediaFile fromTrackEditDTO(TrackEditDTO editDTO, MediaFile mediaFile) {
        MediaFile updatedMediaFile = fromTrackEditDTO(editDTO);
        updatedMediaFile.setId(mediaFile.getId());

        return updatedMediaFile;
    }

    public MediaFile fromMediaFileInfo(MediaFileInfo mediaFileInfo) {
        MediaFormatInfo mediaFormatInfo = mediaFileInfo.getMediaContentInfo().getMediaFormatInfo();

        MediaFile mediaFile = new MediaFile();

        mediaFile.setName(mediaFileInfo.getFileName());
        mediaFile.setFormat(FileUtils.getExtension(mediaFileInfo.getFileName()).toUpperCase());
        mediaFile.setSize(mediaFormatInfo.getSize());
        mediaFile.setBitrate(mediaFormatInfo.getBitRate());
        mediaFile.setDuration(mediaFormatInfo.getDuration());

        return mediaFile;
    }

    public MediaFile fromMediaFileEditDTO(MediaFileEditDTO editDTO) {
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
