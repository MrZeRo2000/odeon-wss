package com.romanpulov.odeonwss.dto;

import java.util.Objects;

public class CompositionValidationDTO {
    private final String artistName;

    public String getArtistName() {
        return artistName;
    }

    private final String artifactTitle;

    public String getArtifactTitle() {
        return artifactTitle;
    }

    private final Long artifactYear;

    public Long getArtifactYear() {
        return artifactYear;
    }

    private final Long compositionNum;

    public Long getCompositionNum() {
        return compositionNum;
    }

    private final String compositionTitle;

    public String getCompositionTitle() {
        return compositionTitle;
    }

    public CompositionValidationDTO(String artistName, String artifactTitle, Long artifactYear, Long compositionNum, String compositionTitle) {
        this.artistName = artistName;
        this.artifactTitle = artifactTitle;
        this.artifactYear = artifactYear;
        this.compositionNum = compositionNum;
        this.compositionTitle = compositionTitle;
    }

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
