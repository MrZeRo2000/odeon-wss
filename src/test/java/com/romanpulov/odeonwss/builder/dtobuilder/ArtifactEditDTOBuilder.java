package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.ArtifactEditDTO;

public class ArtifactEditDTOBuilder extends AbstractClassBuilder<ArtifactEditDTO> {

    public ArtifactEditDTOBuilder() {
        super(ArtifactEditDTO.class);
    }

    public ArtifactEditDTOBuilder withId(long id) {
        instance.setId(id);
        return this;
    }

    public ArtifactEditDTOBuilder withArtifactTypeId(long id) {
        instance.setArtifactTypeId(id);
        return this;
    }

    public ArtifactEditDTOBuilder withArtistId(long id) {
        instance.setArtistId(id);
        return this;
    }

    public ArtifactEditDTOBuilder withTitle(String title) {
        instance.setTitle(title);
        return this;
    }

    public ArtifactEditDTOBuilder withYear(long year) {
        instance.setYear(year);
        return this;
    }

    public ArtifactEditDTOBuilder withDuration(long duration) {
        instance.setDuration(duration);
        return this;
    }

    public ArtifactEditDTOBuilder withSize(long size) {
        instance.setSize(size);
        return this;
    }
}
