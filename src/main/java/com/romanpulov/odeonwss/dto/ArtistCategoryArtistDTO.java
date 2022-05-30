package com.romanpulov.odeonwss.dto;

public class ArtistCategoryArtistDTO {
    private Long id;
    private String artistName;
    private String categoryTypeCode;
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

    public String getCategoryTypeCode() {
        return categoryTypeCode;
    }

    public void setCategoryTypeCode(String categoryTypeCode) {
        this.categoryTypeCode = categoryTypeCode;
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

    public ArtistCategoryArtistDTO(Long id, String artistName, String categoryTypeCode, String categoryName, Long detailId) {
        this.id = id;
        this.artistName = artistName;
        this.categoryTypeCode = categoryTypeCode;
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
                ", categoryTypeCode='" + categoryTypeCode + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", detailId=" + detailId +
                '}';
    }
}
