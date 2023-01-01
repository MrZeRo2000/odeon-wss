package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.DVCategory;

public class EntityDVCategoryBuilder extends AbstractClassBuilder<DVCategory> {
    public EntityDVCategoryBuilder() {
        super(DVCategory.class);
    }

    public EntityDVCategoryBuilder withId(long id) {
        this.instance.setId(id);
        return this;
    }

    public EntityDVCategoryBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }

    public EntityDVCategoryBuilder withMigrationId(long migrationId) {
        this.instance.setMigrationId(migrationId);
        return this;
    }
}
