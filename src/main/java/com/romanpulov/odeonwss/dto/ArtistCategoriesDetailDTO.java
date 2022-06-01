package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.romanpulov.odeonwss.dto.serializer.ArtistTypeDeserializer;
import com.romanpulov.odeonwss.dto.serializer.ArtistTypeSerializer;
import com.romanpulov.odeonwss.entity.ArtistType;

import java.util.ArrayList;
import java.util.List;

public class ArtistCategoriesDetailDTO {
    private Long id;

    @JsonSerialize(using = ArtistTypeSerializer.class)
    @JsonDeserialize(using = ArtistTypeDeserializer.class)
    private ArtistType artistType;

    private String artistName;

    private String artistBiography;

    private String genre;
    private List<String> styles = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArtistType getArtistType() {
        return artistType;
    }

    public void setArtistType(ArtistType artistType) {
        this.artistType = artistType;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistBiography() {
        return artistBiography;
    }

    public void setArtistBiography(String artistBiography) {
        this.artistBiography = artistBiography;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public List<String> getStyles() {
        return styles;
    }

    public void setStyles(List<String> styles) {
        this.styles = styles;
    }
}
