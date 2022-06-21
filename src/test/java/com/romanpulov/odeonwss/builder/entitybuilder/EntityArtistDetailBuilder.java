package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistDetail;

public class EntityArtistDetailBuilder extends AbstractClassBuilder<ArtistDetail> {
    public EntityArtistDetailBuilder() {
        super(ArtistDetail.class);
    }

    public EntityArtistDetailBuilder withArtist(Artist artist) {
        instance.setArtist(artist);
        return this;
    }

    public EntityArtistDetailBuilder withBiography(String biography) {
        instance.setBiography(biography);
        return this;
    }
}
