package com.romanpulov.odeonwss.entitybuilder;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;

public class EntityArtistCategoryBuilder {
    private final ArtistCategory artistCategory;

    public EntityArtistCategoryBuilder() {
        artistCategory = new ArtistCategory();
    }

    public EntityArtistCategoryBuilder withArtist(Artist artist) {
        artistCategory.setArtist(artist);
        return this;
    }

    public EntityArtistCategoryBuilder withType(String type) {
        artistCategory.setType(type);
        return this;
    }

    public EntityArtistCategoryBuilder withName(String name) {
        artistCategory.setName(name);
        return this;
    }

    public ArtistCategory build() {
        return artistCategory;
    }
}
