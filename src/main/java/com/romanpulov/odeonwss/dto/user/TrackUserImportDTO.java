package com.romanpulov.odeonwss.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.dto.MediaFileDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackUserImportDTO {
    private ArtifactDTO artifact;
    private MediaFileDTO mediaFile;
    private IdNameDTO dvType;
    private Long num;
    private List<TrackUserImportDetailDTO> trackDetails;

    public ArtifactDTO getArtifact() {
        return artifact;
    }

    public void setArtifact(ArtifactDTO artifact) {
        this.artifact = artifact;
    }

    public MediaFileDTO getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(MediaFileDTO mediaFile) {
        this.mediaFile = mediaFile;
    }

    public IdNameDTO getDvType() {
        return dvType;
    }

    public void setDvType(IdNameDTO dvType) {
        this.dvType = dvType;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public List<TrackUserImportDetailDTO> getTrackDetails() {
        return trackDetails;
    }

    public void setTrackDetails(List<TrackUserImportDetailDTO> trackDetails) {
        this.trackDetails = trackDetails;
    }

    @Override
    public String toString() {
        return "TrackUserImportDTO{" +
                "artifact=" + artifact +
                ", mediaFileDTO=" + mediaFile +
                ", dvType=" + dvType +
                ", num=" + num +
                ", trackDetails=" + trackDetails +
                '}';
    }
}
