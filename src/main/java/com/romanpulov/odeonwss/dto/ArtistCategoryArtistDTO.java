package com.romanpulov.odeonwss.dto;

import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistType;

public class ArtistCategoryArtistDTO {
    private Long id;
    private String artistName;
    private ArtistType artistType;
    private ArtistCategoryType categoryType;
    private String categoryName;
    public Long detailId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public ArtistType getArtistType() {
        return artistType;
    }

    public void setArtistType(ArtistType artistType) {
        this.artistType = artistType;
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

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public ArtistCategoryArtistDTO(Long id, String artistName, ArtistType artistType, ArtistCategoryType categoryType, String categoryName, Long detailId) {
        this.id = id;
        this.artistName = artistName;
        this.artistType = artistType;
        this.categoryType = categoryType;
        this.categoryName = categoryName;
        this.detailId = detailId;
    }

    public ArtistCategoryArtistDTO() {
    }

    @Override
    public String toString() {
        return "ArtistCategoryArtistDTO{" +
                "id=" + id +
                ", artistName='" + artistName + '\'' +
                ", artistType='" + artistType + '\'' +
                ", categoryType='" + categoryType + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", detailId=" + detailId +
                '}';
    }
}
