package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.ArtistDTOImpl;
import com.romanpulov.odeonwss.entity.ArtistType;

import java.util.List;

public class ArtistDTOBuilder extends AbstractClassBuilder<ArtistDTOImpl> {
    public ArtistDTOBuilder() {
        super(ArtistDTOImpl.class);
    }

    public ArtistDTOBuilder withId(Long id) {
        this.instance.setId(id);
        return this;
    }

    public ArtistDTOBuilder withArtistName(String artistName) {
        this.instance.setArtistName(artistName);
        return this;
    }

    public ArtistDTOBuilder withArtistType(ArtistType artistType) {
        this.instance.setArtistType(artistType);
        return this;
    }

    public ArtistDTOBuilder withGenre(String genre) {
        this.instance.setGenre(genre);
        return this;
    }

    public ArtistDTOBuilder withStyles(List<String> styles) {
        this.instance.setStyles(styles);
        return this;
    }

    public ArtistDTOBuilder withDetailId(long detailId) {
        this.instance.setDetailId(detailId);
        return this;
    }

    public ArtistDTOBuilder withArtistBiography(String artistBiography) {
        this.instance.setArtistBiography(artistBiography);
        return this;
    }
}
