package com.romanpulov.odeonwss.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.IdNameDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackDVTypeUserUpdateDTO {
    private ArtifactDTO artifact;
    private IdNameDTO dvType;

    public ArtifactDTO getArtifact() {
        return artifact;
    }

    public void setArtifact(ArtifactDTO artifact) {
        this.artifact = artifact;
    }

    public IdNameDTO getDvType() {
        return dvType;
    }

    public void setDvType(IdNameDTO dvType) {
        this.dvType = dvType;
    }

    @Override
    public String toString() {
        return "TrackDVTypeUserUpdateDTO{" +
                "artifact=" + artifact +
                ", dvType=" + dvType +
                '}';
    }
}
