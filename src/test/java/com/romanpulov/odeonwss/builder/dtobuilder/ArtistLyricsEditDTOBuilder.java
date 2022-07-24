package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.ArtistLyricsEditDTO;

public class ArtistLyricsEditDTOBuilder extends AbstractClassBuilder<ArtistLyricsEditDTO> {
    public ArtistLyricsEditDTOBuilder() {
        super(ArtistLyricsEditDTO.class);
    }

    public ArtistLyricsEditDTOBuilder withId(long id) {
        instance.setId(id);
        return this;
    }

    public ArtistLyricsEditDTOBuilder withArtistId(long id) {
        instance.setArtistId(id);
        return this;
    }

    public ArtistLyricsEditDTOBuilder withTitle(String title) {
        instance.setTitle(title);
        return this;
    }

    public ArtistLyricsEditDTOBuilder withText(String text) {
        instance.setText(text);
        return this;
    }
}
