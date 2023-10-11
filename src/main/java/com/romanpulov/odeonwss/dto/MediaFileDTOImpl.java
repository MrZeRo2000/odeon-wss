package com.romanpulov.odeonwss.dto;

public class MediaFileDTOImpl implements MediaFileDTO {
    private Long id;
    private Long artifactId;
    private String artifactTitle;
    private String name;
    private String format;
    private Long size;
    private Long bitrate;
    private Long duration;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getArtifactTitle() {
        return artifactTitle;
    }

    public void setArtifactTitle(String artifactTitle) {
        this.artifactTitle = artifactTitle;
    }

    @Override
    public Long getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(Long artifactId) {
        this.artifactId = artifactId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public Long getBitrate() {
        return bitrate;
    }

    public void setBitrate(Long bitrate) {
        this.bitrate = bitrate;
    }

    @Override
    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MediaFileDTOImpl{" +
                "id=" + id +
                ", artifactId=" + artifactId +
                ", name='" + name + '\'' +
                ", format='" + format + '\'' +
                ", size=" + size +
                ", bitrate=" + bitrate +
                ", duration=" + duration +
                '}';
    }
}
