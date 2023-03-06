package com.romanpulov.odeonwss.dto;

import java.util.Set;

public class TrackEditDTO {
    private Long id;

    private Long artifactId;

    private Long diskNum;

    private Long num;

    private Long artistId;

    private String artistName;

    private Long performerArtistId;

    private String performerArtistName;

    private Long dvTypeId;

    private String dvTypeName;

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

    public Long getDvTypeId() {
        return dvTypeId;
    }

    public void setDvTypeId(Long dvTypeId) {
        this.dvTypeId = dvTypeId;
    }

    public String getDvTypeName() {
        return dvTypeName;
    }

    public void setDvTypeName(String dvTypeName) {
        this.dvTypeName = dvTypeName;
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

    public TrackEditDTO(Long id, Long artifactId, Long diskNum, Long num, Long artistId, String artistName, Long performerArtistId, String performerArtistName, Long dvTypeId, String dvTypeName, String title, Long duration, Set<Long> mediaFileIds) {
        this.id = id;
        this.artifactId = artifactId;
        this.diskNum = diskNum;
        this.num = num;
        this.artistId = artistId;
        this.artistName = artistName;
        this.performerArtistId = performerArtistId;
        this.performerArtistName = performerArtistName;
        this.dvTypeId = dvTypeId;
        this.dvTypeName = dvTypeName;
        this.title = title;
        this.duration = duration;
        this.mediaFileIds = mediaFileIds;
    }

    public TrackEditDTO() {
    }

    @Override
    public String toString() {
        return "TrackEditDTO{" +
                "id=" + id +
                ", artifactId=" + artifactId +
                ", diskNum=" + diskNum +
                ", num=" + num +
                ", artistId=" + artistId +
                ", artistName='" + artistName + '\'' +
                ", performerArtistId=" + performerArtistId +
                ", performerArtistName='" + performerArtistName + '\'' +
                ", dvTypeId=" + dvTypeId +
                ", dvTypeName='" + dvTypeName + '\'' +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", mediaFileIds=" + mediaFileIds +
                '}';
    }
}
