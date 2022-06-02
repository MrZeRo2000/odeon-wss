package com.romanpulov.odeonwss.dtobuilder;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.entity.ArtistType;

import java.util.List;

public class ArtistCategoriesDetailDTOBuilder {
    private final ArtistCategoriesDetailDTO dto;

    public ArtistCategoriesDetailDTOBuilder() {
        dto = new ArtistCategoriesDetailDTO();
    }

    public ArtistCategoriesDetailDTOBuilder withId(long id) {
        dto.setId(id);
        return this;
    }

    public ArtistCategoriesDetailDTOBuilder withArtistType(ArtistType artistType) {
        dto.setArtistType(artistType);
        return this;
    }

    public ArtistCategoriesDetailDTOBuilder withArtistName(String artistName) {
        dto.setArtistName(artistName);
        return this;
    }

    public ArtistCategoriesDetailDTOBuilder withArtistBiography(String artistBiography) {
        dto.setArtistBiography(artistBiography);
        return this;
    }

    public ArtistCategoriesDetailDTOBuilder withGenre(String genre) {
        dto.setGenre(genre);
        return this;
    }

    public ArtistCategoriesDetailDTOBuilder withStyles(String... styles) {
        dto.setStyles(List.of(styles));
        return this;
    }

    public ArtistCategoriesDetailDTOBuilder withStyles(List<String> styles) {
        dto.setStyles(styles);
        return this;
    }

    public ArtistCategoriesDetailDTO build() {
        return dto;
    }
}
