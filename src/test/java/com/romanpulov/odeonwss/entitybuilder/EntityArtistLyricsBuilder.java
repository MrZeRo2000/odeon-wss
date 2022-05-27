package com.romanpulov.odeonwss.entitybuilder;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;

public class EntityArtistLyricsBuilder extends AbstractEntityBuilder<ArtistLyrics> {
    public EntityArtistLyricsBuilder() {
        super(ArtistLyrics.class);
    }

    public EntityArtistLyricsBuilder withArtist(Artist artist) {
        entity.setArtist(artist);
        return this;
    }

    public EntityArtistLyricsBuilder withTitle(String title) {
        entity.setTitle(title);
        return this;
    }

    public EntityArtistLyricsBuilder withText(String text) {
        entity.setText(text);
        return this;
    }
}
