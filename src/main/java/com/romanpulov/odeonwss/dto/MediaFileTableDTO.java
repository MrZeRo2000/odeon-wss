package com.romanpulov.odeonwss.dto;

public class MediaFileTableDTO {
    private Long id;

    private String name;

    private String format;

    private Long size;

    private Long bitrate;

    private Long duration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getBitrate() {
        return bitrate;
    }

    public void setBitrate(Long bitrate) {
        this.bitrate = bitrate;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public MediaFileTableDTO(Long id, String name, String format, Long size, Long bitrate, Long duration) {
        this.id = id;
        this.name = name;
        this.format = format;
        this.size = size;
        this.bitrate = bitrate;
        this.duration = duration;
    }

    public MediaFileTableDTO() {
    }

    @Override
    public String toString() {
        return "MediaFileTableDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", format='" + format + '\'' +
                ", size=" + size +
                ", bitrate=" + bitrate +
                ", duration=" + duration +
                '}';
    }
}
