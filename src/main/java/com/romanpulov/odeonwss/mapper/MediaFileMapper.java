package com.romanpulov.odeonwss.mapper;

import com.romanpulov.jutilscore.io.FileUtils;
import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.dto.TrackEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.MediaFormatInfo;
import org.springframework.stereotype.Component;

@Component
public class MediaFileMapper implements EntityDTOMapper<MediaFile, MediaFileDTO> {
    @Override
    public String getEntityName() {
        return "MediaFile";
    }

    @Override
    public MediaFile fromDTO(MediaFileDTO dto) {
        MediaFile entity = new MediaFile();

        // immutable fields
        entity.setId(dto.getId());
        Artifact artifact = new Artifact();
        artifact.setId(dto.getArtifactId());
        entity.setArtifact(artifact);

        // mutable fields
        this.update(entity, dto);

        return entity;
    }

    @Override
    public void update(MediaFile entity, MediaFileDTO dto) {
        entity.setName(dto.getName());
        entity.setFormat(dto.getFormat());
        entity.setSize(dto.getSize());
        entity.setBitrate(dto.getBitrate());
        entity.setDuration(dto.getDuration());
    }

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

}
