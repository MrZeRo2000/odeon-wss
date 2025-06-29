package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.user.TrackSelectedTagsUserUpdateDTO;

import java.util.ArrayList;
import java.util.Collection;

public class TrackSelectedTagsUserUpdateDTOBuilder extends AbstractClassBuilder<TrackSelectedTagsUserUpdateDTO> {
    public TrackSelectedTagsUserUpdateDTOBuilder() {
        super(TrackSelectedTagsUserUpdateDTO.class);
    }

    public TrackSelectedTagsUserUpdateDTOBuilder withArtifact(ArtifactDTO artifact) {
        this.instance.setArtifact(artifact);
        return this;
    }

    public TrackSelectedTagsUserUpdateDTOBuilder withTrackIds(Collection<Long> trackIds) {
        this.instance.setTrackIds(new ArrayList<>(trackIds));
        return this;
    }

    public TrackSelectedTagsUserUpdateDTOBuilder withTags(Collection<String> tags) {
        this.instance.setTags(new ArrayList<>(tags));
        return this;
    }
}
