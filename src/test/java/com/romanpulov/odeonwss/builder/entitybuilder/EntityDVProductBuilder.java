package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.DVCategory;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.entity.DVProduct;

import java.util.Set;

public class EntityDVProductBuilder extends AbstractClassBuilder<DVProduct> {
    public EntityDVProductBuilder() {
        super(DVProduct.class);
    }

    public EntityDVProductBuilder withOrigin(DVOrigin dvOrigin) {
        this.instance.setDvOrigin(dvOrigin);
        return this;
    }

    public EntityDVProductBuilder withTitle(String title) {
        this.instance.setTitle(title);
        return this;
    }

    public EntityDVProductBuilder withOriginalTitle(String originalTitle) {
        this.instance.setOriginalTitle(originalTitle);
        return this;
    }

    public EntityDVProductBuilder withYear(long year) {
        this.instance.setYear(year);
        return this;
    }

    public EntityDVProductBuilder withFrontInfo(String frontInfo) {
        this.instance.setFrontInfo(frontInfo);
        return this;
    }

    public EntityDVProductBuilder withDescription(String description) {
        this.instance.setDescription(description);
        return this;
    }

    public EntityDVProductBuilder withNotes(String notes) {
        this.instance.setNotes(notes);
        return this;
    }

    public EntityDVProductBuilder withCategories(Set<DVCategory> categories) {
        this.instance.setDvCategories(categories);
        return this;
    }
}
