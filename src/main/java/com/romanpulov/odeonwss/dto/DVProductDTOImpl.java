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
    private Boolean hasDescription;
    private String notes;
    private Boolean hasNotes;
    private List<DVCategoryDTO> dvCategories = new ArrayList<>();
    private Boolean hasTracks;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getArtifactTypeId() {
        return artifactTypeId;
    }

    public void setArtifactTypeId(Long artifactTypeId) {
        this.artifactTypeId = artifactTypeId;
    }

    @Override
    public DVOriginDTO getDvOrigin() {
        return dvOrigin;
    }

    public void setDvOrigin(DVOriginDTO dvOrigin) {
        this.dvOrigin = dvOrigin;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    @Override
    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    @Override
    public String getFrontInfo() {
        return frontInfo;
    }

    public void setFrontInfo(String frontInfo) {
        this.frontInfo = frontInfo;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Boolean getHasDescription() {
        return hasDescription;
    }

    public void setHasDescription(Boolean hasDescription) {
        this.hasDescription = hasDescription;
    }

    @Override
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public Boolean getHasNotes() {
        return hasNotes;
    }

    public void setHasNotes(Boolean hasNotes) {
        this.hasNotes = hasNotes;
    }

    @Override
    public List<DVCategoryDTO> getDvCategories() {
        return dvCategories;
    }

    public void setDvCategories(List<DVCategoryDTO> dvCategories) {
        this.dvCategories = dvCategories;
    }

    @Override
    public Boolean getHasTracks() {
        return hasTracks;
    }

    public void setHasTracks(Boolean hasTracks) {
        this.hasTracks = hasTracks;
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
                ", hasTracks=" + hasTracks +
                '}';
    }
}
