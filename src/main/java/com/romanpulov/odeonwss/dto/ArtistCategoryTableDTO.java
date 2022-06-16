package com.romanpulov.odeonwss.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistCategoryTableDTO that = (ArtistCategoryTableDTO) o;
        return id.equals(that.id) && artistName.equals(that.artistName) && artistType.equals(that.artistType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, artistName, artistType);
    }

    @Override
    public String toString() {
        return "ArtistCategoryTableDTO{" +
                "id=" + id +
                ", artistName='" + artistName + '\'' +
                ", artistType='" + artistType + '\'' +
                ", genre='" + genre + '\'' +
                ", styles=" + styles +
                ", detailId=" + detailId +
                '}';
    }
}
