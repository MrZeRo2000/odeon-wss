package com.romanpulov.odeonwss.entitybuilder;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistDetail;

public class EntityArtistDetailBuilder extends AbstractEntityBuilder<ArtistDetail> {
    public EntityArtistDetailBuilder() {
        super(ArtistDetail.class);
    }

    public EntityArtistDetailBuilder withArtist(Artist artist) {
        entity.setArtist(artist);
        return this;
    }

    public EntityArtistDetailBuilder withBiography(String biography) {
        entity.setBiography(biography);
        return this;
    }
}
