package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.DVCategoryDTOImpl;

public class DVCategoryDTOBuilder extends AbstractClassBuilder<DVCategoryDTOImpl> {
    public DVCategoryDTOBuilder() {
        super(DVCategoryDTOImpl.class);
    }

    public DVCategoryDTOBuilder withId(Long id) {
        this.instance.setId(id);
        return this;
    }

    public DVCategoryDTOBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }
}
