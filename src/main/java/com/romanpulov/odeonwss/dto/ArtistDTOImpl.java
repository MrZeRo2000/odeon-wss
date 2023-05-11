package com.romanpulov.odeonwss.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArtistDTOImpl implements ArtistDTO {
    private Long id;
    private String artistName;
    private String artistType;
    private String genre;
    private List<String> styles = new ArrayList<>();
    private Long detailId;
    private Boolean hasLyrics;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public String getArtistType() {
        return artistType;
    }

    public void setArtistType(String artistType) {
        this.artistType = artistType;
    }

    @Override
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public List<String> getStyles() {
        return styles;
    }

    public void setStyles(List<String> styles) {
        this.styles = styles;
    }

    @Override
    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    @Override
    public Boolean getHasLyrics() {
        return hasLyrics;
    }

    public void setHasLyrics(Boolean hasLyrics) {
        this.hasLyrics = hasLyrics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistDTOImpl artistDTO = (ArtistDTOImpl) o;
        return Objects.equals(id, artistDTO.id) && Objects.equals(artistName, artistDTO.artistName) && Objects.equals(artistType, artistDTO.artistType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, artistName, artistType);
    }

    @Override
    public String toString() {
        return "ArtistDTOImpl{" +
                "id=" + id +
                ", artistName='" + artistName + '\'' +
                ", artistType='" + artistType + '\'' +
                ", genre='" + genre + '\'' +
                ", styles=" + styles +
                ", detailId=" + detailId +
                ", hasLyrics=" + hasLyrics +
                '}';
    }
}
