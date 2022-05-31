package com.romanpulov.odeonwss.dto;

import java.util.ArrayList;
import java.util.List;

public class ArtistCategoryArtistListDTO {
    private final Long id;
    private final String artistName;
    private String genre;
    private final List<String> styles = new ArrayList<>();
    private final Long detailId;

    public Long getId() {
        return id;
    }

    public String getArtistName() {
        return artistName;
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

    public ArtistCategoryArtistListDTO(Long id, String artistName, Long detailId) {
        this.id = id;
        this.artistName = artistName;
        this.detailId = detailId;
    }
}
