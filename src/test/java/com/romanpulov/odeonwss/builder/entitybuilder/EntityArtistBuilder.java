package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import com.romanpulov.odeonwss.entity.ArtistDetail;
import com.romanpulov.odeonwss.entity.ArtistType;

import java.util.List;

public class EntityArtistBuilder extends AbstractClassBuilder<Artist> {

    public EntityArtistBuilder() {
        super(Artist.class);
    }

    public EntityArtistBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }

    public EntityArtistBuilder withType(ArtistType type) {
        this.instance.setType(type);
        return this;
    }

    public EntityArtistBuilder withCategories(List<ArtistCategory> categories) {
        this.instance.setArtistCategories(categories);
        return this;
    }

    public EntityArtistBuilder withDetail(ArtistDetail artistDetail) {
        this.instance.setArtistDetails(List.of(artistDetail));
        return this;
    }
}
