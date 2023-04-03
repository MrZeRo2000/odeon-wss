package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.DVProductDTOImpl;

public class DVProductDTOBuilder extends AbstractClassBuilder<DVProductDTOImpl> {
    public DVProductDTOBuilder() {
        super(DVProductDTOImpl.class);
    }

    public DVProductDTOBuilder withId(Long id) {
        this.instance.setId(id);
        return this;
    }

    public DVProductDTOBuilder withArtifactTypeId(Long artifactTypeId) {
        this.instance.setArtifactTypeId(artifactTypeId);
        return this;
    }
}
