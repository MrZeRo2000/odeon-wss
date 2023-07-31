package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.DVType;

public class EntityDvTypeBuilder extends AbstractClassBuilder<DVType> {
    public EntityDvTypeBuilder() {
        super(DVType.class);
    }

    public EntityDvTypeBuilder withId(long id) {
        this.instance.setId(id);
        return this;
    }

    public EntityDvTypeBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }
}
