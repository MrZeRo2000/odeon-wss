package com.romanpulov.odeonwss.dto;

import java.util.Objects;

public class CompositionValidationDTO {
    private String artistName;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    private String artifactTitle;

    public String getArtifactTitle() {
        return artifactTitle;
    }

    public void setArtifactTitle(String artifactTitle) {
        this.artifactTitle = artifactTitle;
    }

    private Long artifactYear;

    public Long getArtifactYear() {
        return artifactYear;
    }

    public void setArtifactYear(Long artifactYear) {
        this.artifactYear = artifactYear;
    }

    private Long compositionNum;

    public Long getCompositionNum() {
        return compositionNum;
    }

    public void setCompositionNum(Long compositionNum) {
        this.compositionNum = compositionNum;
    }

    private String compositionTitle;

    public String getCompositionTitle() {
        return compositionTitle;
    }

    public void setCompositionTitle(String compositionTitle) {
        this.compositionTitle = compositionTitle;
    }

    public CompositionValidationDTO(String artistName, String artifactTitle, Long artifactYear, Long compositionNum, String compositionTitle) {
        this.artistName = artistName;
        this.artifactTitle = artifactTitle;
        this.artifactYear = artifactYear;
        this.compositionNum = compositionNum;
        this.compositionTitle = compositionTitle;
    }

    public CompositionValidationDTO() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositionValidationDTO that = (CompositionValidationDTO) o;
        return artistName.equals(that.artistName) && artifactTitle.equals(that.artifactTitle) && artifactYear.equals(that.artifactYear) && compositionNum.equals(that.compositionNum) && compositionTitle.equals(that.compositionTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistName, artifactTitle, artifactYear, compositionNum, compositionTitle);
    }

    @Override
    public String toString() {
        return "CompositionValidationDTO{" +
                "artistName='" + artistName + '\'' +
                ", artifactTitle='" + artifactTitle + '\'' +
                ", artifactYear=" + artifactYear +
                ", compositionNum=" + compositionNum +
                ", compositionTitle='" + compositionTitle + '\'' +
                '}';
    }
}
