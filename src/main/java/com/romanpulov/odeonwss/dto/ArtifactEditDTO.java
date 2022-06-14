package com.romanpulov.odeonwss.dto;

public class ArtifactEditDTO {
    private Long id;
    private Long artifactTypeId;
    private Long artistId;
    private String title;
    private Long year;
    private Long duration;
    private Long size;

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

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
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

    public ArtifactEditDTO(Long id, Long artifactTypeId, Long artistId, String title, Long year, Long duration, Long size) {
        this.id = id;
        this.artifactTypeId = artifactTypeId;
        this.artistId = artistId;
        this.title = title;
        this.year = year;
        this.duration = duration;
        this.size = size;
    }

    public ArtifactEditDTO() {
    }
}
