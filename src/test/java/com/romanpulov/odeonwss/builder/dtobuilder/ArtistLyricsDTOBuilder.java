package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.ArtistLyricsDTOImpl;

public class ArtistLyricsDTOBuilder extends AbstractClassBuilder<ArtistLyricsDTOImpl> {
    public ArtistLyricsDTOBuilder() {
        super(ArtistLyricsDTOImpl.class);
    }

    public ArtistLyricsDTOBuilder withId(long id) {
        instance.setId(id);
        return this;
    }

    public ArtistLyricsDTOBuilder withArtistId(long id) {
        instance.setArtistId(id);
        return this;
    }

    public ArtistLyricsDTOBuilder withTitle(String title) {
        instance.setTitle(title);
        return this;
    }

    public ArtistLyricsDTOBuilder withText(String text) {
        instance.setText(text);
        return this;
    }
}
