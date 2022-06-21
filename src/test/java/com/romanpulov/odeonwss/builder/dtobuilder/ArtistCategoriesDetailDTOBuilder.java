package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.entity.ArtistType;

import java.util.List;

public class ArtistCategoriesDetailDTOBuilder extends AbstractClassBuilder<ArtistCategoriesDetailDTO> {
    public ArtistCategoriesDetailDTOBuilder() {
        super(ArtistCategoriesDetailDTO.class);
    }

    public ArtistCategoriesDetailDTOBuilder withId(long id) {
        instance.setId(id);
        return this;
    }

    public ArtistCategoriesDetailDTOBuilder withArtistType(ArtistType artistType) {
        instance.setArtistType(artistType);
        return this;
    }

    public ArtistCategoriesDetailDTOBuilder withArtistName(String artistName) {
        instance.setArtistName(artistName);
        return this;
    }

    public ArtistCategoriesDetailDTOBuilder withArtistBiography(String artistBiography) {
        instance.setArtistBiography(artistBiography);
        return this;
    }

    public ArtistCategoriesDetailDTOBuilder withGenre(String genre) {
        instance.setGenre(genre);
        return this;
    }

    public ArtistCategoriesDetailDTOBuilder withStyles(String... styles) {
        instance.setStyles(List.of(styles));
        return this;
    }

    public ArtistCategoriesDetailDTOBuilder withStyles(List<String> styles) {
        instance.setStyles(styles);
        return this;
    }
}
