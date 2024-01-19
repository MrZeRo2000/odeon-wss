package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.DVType;

public class EntityDVTypeBuilder extends AbstractClassBuilder<DVType> {
    public EntityDVTypeBuilder() {
        super(DVType.class);
    }

    public EntityDVTypeBuilder withId(long id) {
        this.instance.setId(id);
        return this;
    }

    public EntityDVTypeBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }
}
