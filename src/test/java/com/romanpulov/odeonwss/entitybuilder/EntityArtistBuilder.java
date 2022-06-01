package com.romanpulov.odeonwss.entitybuilder;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;

public class EntityArtistBuilder {
    private final Artist artist;

    public EntityArtistBuilder() {
        artist = new Artist();
    }

    public EntityArtistBuilder withName(String name) {
        artist.setName(name);
        return this;
    }

    public EntityArtistBuilder withType(ArtistType type) {
        artist.setType(type);
        return this;
    }

    public Artist build() {
        return artist;
    }
}
