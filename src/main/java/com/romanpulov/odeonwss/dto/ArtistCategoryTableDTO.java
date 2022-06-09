package com.romanpulov.odeonwss.dto;

import java.util.ArrayList;
import java.util.List;

public class ArtistCategoryTableDTO {
    private final Long id;
    private final String artistName;
    private final String artistType;
    private String genre;
    private final List<String> styles = new ArrayList<>();
    private final Long detailId;

    public Long getId() {
        return id;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistType() {
        return artistType;
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

    public Long getDetailId() {
        return detailId;
    }

    public ArtistCategoryTableDTO(Long id, String artistName, String artistType, Long detailId) {
        this.id = id;
        this.artistName = artistName;
        this.artistType = artistType;
        this.detailId = detailId;
    }
}
