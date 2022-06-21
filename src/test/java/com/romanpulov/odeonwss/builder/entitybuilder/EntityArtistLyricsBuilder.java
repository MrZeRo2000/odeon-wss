package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;

public class EntityArtistLyricsBuilder extends AbstractClassBuilder<ArtistLyrics> {
    public EntityArtistLyricsBuilder() {
        super(ArtistLyrics.class);
    }

    public EntityArtistLyricsBuilder withArtist(Artist artist) {
        instance.setArtist(artist);
        return this;
    }

    public EntityArtistLyricsBuilder withTitle(String title) {
        instance.setTitle(title);
        return this;
    }

    public EntityArtistLyricsBuilder withText(String text) {
        instance.setText(text);
        return this;
    }
}
