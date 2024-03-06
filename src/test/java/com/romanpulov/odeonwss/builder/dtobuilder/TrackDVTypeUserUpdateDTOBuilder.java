package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.dto.user.TrackDVTypeUserUpdateDTO;

public class TrackDVTypeUserUpdateDTOBuilder extends AbstractClassBuilder<TrackDVTypeUserUpdateDTO> {
    public TrackDVTypeUserUpdateDTOBuilder() {
        super(TrackDVTypeUserUpdateDTO.class);
    }

    public TrackDVTypeUserUpdateDTOBuilder withArtifact(ArtifactDTO artifact) {
        this.instance.setArtifact(artifact);
        return this;
    }

    public TrackDVTypeUserUpdateDTOBuilder withDVType(IdNameDTO dvType) {
        this.instance.setDvType(dvType);
        return this;
    }
}
