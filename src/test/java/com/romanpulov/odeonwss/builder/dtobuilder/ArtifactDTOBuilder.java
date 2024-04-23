package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.ArtifactDTOImpl;
import com.romanpulov.odeonwss.dto.ArtifactTypeDTO;
import com.romanpulov.odeonwss.dto.ArtistDTO;

import java.util.List;

public class ArtifactDTOBuilder extends AbstractClassBuilder<ArtifactDTOImpl> {

    public ArtifactDTOBuilder() {
        super(ArtifactDTOImpl.class);
    }

    public ArtifactDTOBuilder withId(long id) {
        instance.setId(id);
        return this;
    }

    public ArtifactDTOBuilder withArtifactType(ArtifactTypeDTO artifactType) {
        instance.setArtifactType(artifactType);
        return this;
    }

    public ArtifactDTOBuilder withArtifactTypeId(long id) {
        instance.setArtifactType(new ArtifactTypeDTOBuilder().withId(id).build());
        return this;
    }


    public ArtifactDTOBuilder withArtist(ArtistDTO artist) {
        instance.setArtist(artist);
        return this;
    }

    public ArtifactDTOBuilder withPerformerArtist(ArtistDTO performerArtist) {
        instance.setPerformerArtist(performerArtist);
        return this;
    }

    public ArtifactDTOBuilder withTitle(String title) {
        instance.setTitle(title);
        return this;
    }

    public ArtifactDTOBuilder withYear(long year) {
        instance.setYear(year);
        return this;
    }

    public ArtifactDTOBuilder withDuration(long duration) {
        instance.setDuration(duration);
        return this;
    }

    public ArtifactDTOBuilder withSize(long size) {
        instance.setSize(size);
        return this;
    }

    public ArtifactDTOBuilder withTags(List<String> tags) {
        instance.setTags(tags);
        return this;
    }
}
