package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.DVOriginDTOImpl;

public class DVOriginDTOBuilder extends AbstractClassBuilder<DVOriginDTOImpl> {
    public DVOriginDTOBuilder() {
        super(DVOriginDTOImpl.class);
    }

    public DVOriginDTOBuilder withId(Long id) {
        this.instance.setId(id);
        return this;
    }

    public DVOriginDTOBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }
}
