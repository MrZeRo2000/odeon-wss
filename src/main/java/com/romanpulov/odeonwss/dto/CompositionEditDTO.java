package com.romanpulov.odeonwss.dto;

import java.util.Set;

public class CompositionEditDTO {
    private Long id;

    private Long artifactId;

    private Long diskNum;

    private Long num;

    private Long artistId;

    private String artistName;

    private String title;

    private Long duration;

    private Set<Long> mediaFileIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(Long artifactId) {
        this.artifactId = artifactId;
    }

    public Long getDiskNum() {
        return diskNum;
    }

    public void setDiskNum(Long diskNum) {
        this.diskNum = diskNum;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Set<Long> getMediaFileIds() {
        return mediaFileIds;
    }

    public void setMediaFiles(Set<Long> mediaFileIds) {
        this.mediaFileIds = mediaFileIds;
    }

    public CompositionEditDTO(Long id, Long artifactId, Long diskNum, Long num, Long artistId, String artistName, String title, Long duration, Set<Long> mediaFileIds) {
        this.id = id;
        this.artifactId = artifactId;
        this.diskNum = diskNum;
        this.num = num;
        this.artistId = artistId;
        this.artistName = artistName;
        this.title = title;
        this.duration = duration;
        this.mediaFileIds = mediaFileIds;
    }

    public CompositionEditDTO() {
    }

    @Override
    public String toString() {
        return "CompositionEditDTO{" +
                "id=" + id +
                ", artifactId=" + artifactId +
                ", diskNum=" + diskNum +
                ", num=" + num +
                ", artistId=" + artistId +
                ", artistName='" + artistName + '\'' +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", mediaFileIds=" + mediaFileIds +
                '}';
    }
}
