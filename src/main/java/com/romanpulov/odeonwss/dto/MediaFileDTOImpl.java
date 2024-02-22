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
    private Long width;
    private Long height;
    private Long hasExtra;
    private String extra;

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
    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    @Override
    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    @Override
    public Long getHasExtra() {
        return hasExtra;
    }

    public void setHasExtra(Long hasExtra) {
        this.hasExtra = hasExtra;
    }

    @Override
    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "MediaFileDTOImpl{" +
                "id=" + id +
                ", artifactId=" + artifactId +
                ", artifactTitle='" + artifactTitle + '\'' +
                ", name='" + name + '\'' +
                ", format='" + format + '\'' +
                ", size=" + size +
                ", bitrate=" + bitrate +
                ", duration=" + duration +
                ", width=" + width +
                ", height=" + height +
                ", hasExtra=" + hasExtra +
                ", extra='" + extra + '\'' +
                '}';
    }
}
