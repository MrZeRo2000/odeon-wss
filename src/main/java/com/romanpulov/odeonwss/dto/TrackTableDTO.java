package com.romanpulov.odeonwss.dto;

public class TrackTableDTO {
    private Long id;

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

    public String getArtistName() {
        return artistName;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
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

    public TrackTableDTO(Long id, Long diskNum, Long num, Long artistId, String artistName, Long performerArtistId, String performerArtistName, Long dvTypeId, String dvTypeName, String title, Long duration, Long size, Long bitrate, String fileName) {
        this.id = id;
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
        this.size = size;
        this.bitrate = bitrate;
        this.fileName = fileName;
    }

    public TrackTableDTO() {
    }

    @Override
    public String toString() {
        return "TrackTableDTO{" +
                "id=" + id +
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
                ", size=" + size +
                ", bitrate=" + bitrate +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}