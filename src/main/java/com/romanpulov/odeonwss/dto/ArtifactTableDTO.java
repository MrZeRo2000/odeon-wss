package com.romanpulov.odeonwss.dto;

import java.time.LocalDate;

public class ArtifactTableDTO {
    private Long id;

    private String artifactTypeName;

    private String artistName;

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

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
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

    public ArtifactTableDTO(Long id, String artifactTypeName, String artistName, String title, Long year, Long duration, Long size, LocalDate insertDate) {
        this.id = id;
        this.artifactTypeName = artifactTypeName;
        this.artistName = artistName;
        this.title = title;
        this.year = year;
        this.duration = duration;
        this.size = size;
        this.insertDate = insertDate;
    }

    public ArtifactTableDTO() {
    }
}
