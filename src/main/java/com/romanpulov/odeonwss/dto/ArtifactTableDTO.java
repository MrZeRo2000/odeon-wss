package com.romanpulov.odeonwss.dto;

import com.romanpulov.odeonwss.entity.ArtistType;

import java.time.LocalDate;

public class ArtifactTableDTO {
    private Long id;

    private String artifactTypeName;

    private String artistTypeCode;

    private String artistName;

    private String performerArtistName;

    private String title;

    private Long year;

    private Long duration;

    private Long size;

    private LocalDate insertDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtifactTypeName() {
        return artifactTypeName;
    }

    public void setArtifactTypeName(String artifactTypeName) {
        this.artifactTypeName = artifactTypeName;
    }

    public String getArtistTypeCode() {
        return artistTypeCode;
    }

    public void setArtistTypeCode(String artistTypeCode) {
        this.artistTypeCode = artistTypeCode;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getPerformerArtistName() {
        return performerArtistName;
    }

    public void setPerformerArtistName(String performerArtistName) {
        this.performerArtistName = performerArtistName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public LocalDate getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(LocalDate insertDate) {
        this.insertDate = insertDate;
    }

    public ArtifactTableDTO(Long id, String artifactTypeName, ArtistType artistType, String artistName, String performerArtistName, String title, Long year, Long duration, Long size, LocalDate insertDate) {
        this.id = id;
        this.artifactTypeName = artifactTypeName;
        this.artistTypeCode = artistType == null ? null : artistType.getCode();
        this.artistName = artistName;
        this.performerArtistName = performerArtistName;
        this.title = title;
        this.year = year;
        this.duration = duration;
        this.size = size;
        this.insertDate = insertDate;
    }

    public ArtifactTableDTO() {
    }

    @Override
    public String toString() {
        return "ArtifactTableDTO{" +
                "id=" + id +
                ", artifactTypeName='" + artifactTypeName + '\'' +
                ", artistTypeCode='" + artistTypeCode + '\'' +
                ", artistName='" + artistName + '\'' +
                ", performerArtistName='" + performerArtistName + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", duration=" + duration +
                ", size=" + size +
                ", insertDate=" + insertDate +
                '}';
    }
}
