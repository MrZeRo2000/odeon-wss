package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.IdNameDTOImpl;

public class IdNameDTOBuilder extends AbstractClassBuilder<IdNameDTOImpl> {
    public IdNameDTOBuilder() {
        super(IdNameDTOImpl.class);
    }

    public IdNameDTOBuilder withId(long id) {
        this.instance.setId(id);
        return this;
    }

    public IdNameDTOBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }
}
