package com.romanpulov.odeonwss.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.IdNameDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackSelectedDVTypeUserUpdateDTO {
    private ArtifactDTO artifact;
    private List<Long> trackIds;
    private IdNameDTO dvType;

    public ArtifactDTO getArtifact() {
        return artifact;
    }

    public void setArtifact(ArtifactDTO artifact) {
        this.artifact = artifact;
    }

    public List<Long> getTrackIds() {
        return trackIds;
    }

    public void setTrackIds(List<Long> trackIds) {
        this.trackIds = trackIds;
    }

    public IdNameDTO getDvType() {
        return dvType;
    }

    public void setDvType(IdNameDTO dvType) {
        this.dvType = dvType;
    }

    @Override
    public String toString() {
        return "TrackSelectedDVTypeUserUpdateDTO{" +
                "artifact=" + getArtifact() +
                ", trackIds=" + getTrackIds() +
                ", dvType=" + getDvType() +
                '}';
    }
}
