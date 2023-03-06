package com.romanpulov.odeonwss.dto;

import java.util.Objects;

public class TrackValidationDTO {
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

    private Long trackNum;

    public Long getTrackNum() {
        return trackNum;
    }

    public void setTrackNum(Long trackNum) {
        this.trackNum = trackNum;
    }

    private String trackTitle;

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public TrackValidationDTO(String artistName, String artifactTitle, Long artifactYear, Long trackNum, String trackTitle) {
        this.artistName = artistName;
        this.artifactTitle = artifactTitle;
        this.artifactYear = artifactYear;
        this.trackNum = trackNum;
        this.trackTitle = trackTitle;
    }

    public TrackValidationDTO() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackValidationDTO that = (TrackValidationDTO) o;
        return artistName.equals(that.artistName) && artifactTitle.equals(that.artifactTitle) && artifactYear.equals(that.artifactYear) && trackNum.equals(that.trackNum) && trackTitle.equals(that.trackTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistName, artifactTitle, artifactYear, trackNum, trackTitle);
    }

    @Override
    public String toString() {
        return "TrackValidationDTO{" +
                "artistName='" + artistName + '\'' +
                ", artifactTitle='" + artifactTitle + '\'' +
                ", artifactYear=" + artifactYear +
                ", trackNum=" + trackNum +
                ", trackTitle='" + trackTitle + '\'' +
                '}';
    }
}
