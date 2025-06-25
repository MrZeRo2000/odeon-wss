package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.dto.user.TrackSelectedDVTypeUserUpdateDTO;

import java.util.List;

public class TrackSelectedDVTypeUserUpdateDTOBuilder extends AbstractClassBuilder<TrackSelectedDVTypeUserUpdateDTO> {
    public TrackSelectedDVTypeUserUpdateDTOBuilder() {
        super(TrackSelectedDVTypeUserUpdateDTO.class);
    }

    public TrackSelectedDVTypeUserUpdateDTOBuilder withArtifact(ArtifactDTO artifact) {
        this.instance.setArtifact(artifact);
        return this;
    }

    public TrackSelectedDVTypeUserUpdateDTOBuilder withTrackIds(List<Long> trackIds) {
        this.instance.setTrackIds(trackIds);
        return this;
    }

    public TrackSelectedDVTypeUserUpdateDTOBuilder withDVType(IdNameDTO dvType) {
        this.instance.setDvType(dvType);
        return this;
    }
}
