package com.romanpulov.odeonwss.dto;

import java.util.Set;

public class CompositionEditDTO {
    private Long id;

    private Long artifactId;

    private Long diskNum;

    private Long num;

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

    public CompositionEditDTO(Long id, Long artifactId, Long diskNum, Long num, String title, Long duration, Set<Long> mediaFileIds) {
        this.id = id;
        this.artifactId = artifactId;
        this.diskNum = diskNum;
        this.num = num;
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
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", mediaFileIds='" + mediaFileIds + '\'' +
                '}';
    }
}
