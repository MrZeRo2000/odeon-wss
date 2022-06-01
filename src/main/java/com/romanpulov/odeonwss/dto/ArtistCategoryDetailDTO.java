package com.romanpulov.odeonwss.dto;

import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistType;

public class ArtistCategoryDetailDTO{
    private Long id;
    private ArtistType artistType;
    private String artistName;
    private String artistBiography;
    private ArtistCategoryType categoryType;
    private String categoryName;

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

    public ArtistCategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(ArtistCategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArtistCategoryDetailDTO(
            Long id,
            ArtistType artistType,
            String artistName,
            String artistBiography,
            ArtistCategoryType categoryType,
            String categoryName
    ) {
        this.id = id;
        this.artistType = artistType;
        this.artistName = artistName;
        this.artistBiography = artistBiography;
        this.categoryType = categoryType;
        this.categoryName = categoryName;
    }
}
