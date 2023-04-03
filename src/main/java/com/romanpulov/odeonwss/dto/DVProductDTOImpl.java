package com.romanpulov.odeonwss.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DVProductDTOImpl implements DVProductDTO {
    private Long id;
    private Long artifactTypeId;
    private DVOriginDTO dvOrigin;
    private String title;
    private String originalTitle;
    private Long year;
    private String frontInfo;
    private String description;
    private String notes;
    private List<DVCategoryDTO> dvCategories = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArtifactTypeId() {
        return artifactTypeId;
    }

    public void setArtifactTypeId(Long artifactTypeId) {
        this.artifactTypeId = artifactTypeId;
    }

    public DVOriginDTO getDvOrigin() {
        return dvOrigin;
    }

    public void setDvOriginId(DVOriginDTO dvOrigin) {
        this.dvOrigin = dvOrigin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public String getFrontInfo() {
        return frontInfo;
    }

    public void setFrontInfo(String frontInfo) {
        this.frontInfo = frontInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<DVCategoryDTO> getDvCategories() {
        return dvCategories;
    }

    public void setDvCategories(List<DVCategoryDTO> dvCategories) {
        this.dvCategories = dvCategories;
    }

    public DVProductDTOImpl() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DVProductDTOImpl that = (DVProductDTOImpl) o;
        return Objects.equals(id, that.id) && Objects.equals(artifactTypeId, that.artifactTypeId) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, artifactTypeId, title);
    }

    @Override
    public String toString() {
        return "DVProductDTOImpl{" +
                "id=" + id +
                ", artifactTypeId=" + artifactTypeId +
                ", dvOrigin=" + dvOrigin +
                ", title='" + title + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", year=" + year +
                ", frontInfo='" + frontInfo + '\'' +
                ", description='" + description + '\'' +
                ", notes='" + notes + '\'' +
                ", dvCategories=" + dvCategories +
                '}';
    }
}
