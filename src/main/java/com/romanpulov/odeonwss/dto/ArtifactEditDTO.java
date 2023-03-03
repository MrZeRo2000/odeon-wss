package com.romanpulov.odeonwss.dto;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;

public class ArtifactEditDTO {
    private Long id;
    private Long artifactTypeId;
    private String artistTypeCode;
    private Long artistId;
    private String artistName;
    private Long performerArtistId;
    private String performerArtistName;
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

    public String getArtistTypeCode() {
        return artistTypeCode;
    }

    public void setArtistTypeCode(String artistTypeCode) {
        this.artistTypeCode = artistTypeCode;
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

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Long getPerformerArtistId() {
        return performerArtistId;
    }

    public void setPerformerArtistId(Long performerArtistId) {
        this.performerArtistId = performerArtistId;
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

    public ArtifactEditDTO(Long id, Long artifactTypeId, Artist artist, Artist performerArtist, String title, Long year, Long duration, Long size) {
        this.id = id;
        this.artifactTypeId = artifactTypeId;
        this.artistTypeCode = artist == null ? null : artist.getType() == null ? null : artist.getType().getCode();
        this.artistId = artist == null ? null : artist.getId();
        this.artistName = artist == null ? null : artist.getName();
        this.performerArtistId = performerArtist == null ? null : performerArtist.getId();
        this.performerArtistName = performerArtist == null ? null : performerArtist.getName();
        this.title = title;
        this.year = year;
        this.duration = duration;
        this.size = size;
    }

    public ArtifactEditDTO() {
    }

    @Override
    public String toString() {
        return "ArtifactEditDTO{" +
                "id=" + id +
                ", artifactTypeId=" + artifactTypeId +
                ", artistTypeCode='" + artistTypeCode + '\'' +
                ", artistId=" + artistId +
                ", artistName='" + artistName + '\'' +
                ", performerArtistId=" + performerArtistId +
                ", performerArtistName='" + performerArtistName + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", duration=" + duration +
                ", size=" + size +
                '}';
    }
}
