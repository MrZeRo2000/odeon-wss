package com.romanpulov.odeonwss.dto;

public class CompositionEditDTO {
    private Long id;

    private Long diskNum;

    private Long num;

    private String title;

    private Long duration;

    private String mediaName;

    private String mediaFormat;

    private Long mediaSize;

    private Long mediaBitrate;

    private Long mediaDuration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaFormat() {
        return mediaFormat;
    }

    public void setMediaFormat(String mediaFormat) {
        this.mediaFormat = mediaFormat;
    }

    public Long getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(Long mediaSize) {
        this.mediaSize = mediaSize;
    }

    public Long getMediaBitrate() {
        return mediaBitrate;
    }

    public void setMediaBitrate(Long mediaBitrate) {
        this.mediaBitrate = mediaBitrate;
    }

    public Long getMediaDuration() {
        return mediaDuration;
    }

    public void setMediaDuration(Long mediaDuration) {
        this.mediaDuration = mediaDuration;
    }

    public CompositionEditDTO(Long id, Long diskNum, Long num, String title, Long duration, String mediaName, String mediaFormat, Long mediaSize, Long mediaBitrate, Long mediaDuration) {
        this.id = id;
        this.diskNum = diskNum;
        this.num = num;
        this.title = title;
        this.duration = duration;
        this.mediaName = mediaName;
        this.mediaFormat = mediaFormat;
        this.mediaSize = mediaSize;
        this.mediaBitrate = mediaBitrate;
        this.mediaDuration = mediaDuration;
    }

    public CompositionEditDTO() {
    }

    @Override
    public String toString() {
        return "CompositionEditDTO{" +
                "id=" + id +
                ", diskNum=" + diskNum +
                ", num=" + num +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", mediaName='" + mediaName + '\'' +
                ", mediaFormat='" + mediaFormat + '\'' +
                ", mediaSize=" + mediaSize +
                ", mediaBitrate=" + mediaBitrate +
                ", mediaDuration=" + mediaDuration +
                '}';
    }
}
