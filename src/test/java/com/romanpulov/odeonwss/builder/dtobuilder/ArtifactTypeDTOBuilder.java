package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.ArtifactTypeDTOImpl;

public class ArtifactTypeDTOBuilder extends AbstractClassBuilder<ArtifactTypeDTOImpl> {
    public ArtifactTypeDTOBuilder() {
        super(ArtifactTypeDTOImpl.class);
    }

    public ArtifactTypeDTOBuilder withId(long id) {
        this.instance.setId(id);
        return this;
    }

    public ArtifactTypeDTOBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }
}
