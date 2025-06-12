package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.Tag;

public class EntityTagBuilder extends AbstractClassBuilder<Tag> {
    public EntityTagBuilder() {
        super(Tag.class);
    }

    public EntityTagBuilder withName(String name) {
        instance.setName(name);
        return this;
    }
}
