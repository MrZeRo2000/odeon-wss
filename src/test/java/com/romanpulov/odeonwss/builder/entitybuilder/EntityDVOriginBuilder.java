package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.DVOrigin;

public class EntityDVOriginBuilder extends AbstractClassBuilder<DVOrigin> {
    public EntityDVOriginBuilder() {
        super(DVOrigin.class);
    }

    public EntityDVOriginBuilder withId(long id) {
        this.instance.setId(id);
        return this;
    }

    public EntityDVOriginBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }
}
