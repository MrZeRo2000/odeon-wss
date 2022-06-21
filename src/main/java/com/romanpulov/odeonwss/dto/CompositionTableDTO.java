package com.romanpulov.odeonwss.dto;

public class CompositionTableDTO {
    private Long id;

    private Long diskNum;

    private Long num;

    private String title;

    private Long duration;

    private Long size;

    private Long bitrate;

    private String fileName;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public CompositionTableDTO(Long id, Long diskNum, Long num, String title, Long duration, Long size, Long bitrate, String fileName) {
        this.id = id;
        this.diskNum = diskNum;
        this.num = num;
        this.title = title;
        this.duration = duration;
        this.size = size;
        this.bitrate = bitrate;
        this.fileName = fileName;
    }

    public CompositionTableDTO() {
    }

    @Override
    public String toString() {
        return "CompositionTableDTO{" +
                "id=" + id +
                ", diskNum=" + diskNum +
                ", num=" + num +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", bitrate=" + bitrate +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
