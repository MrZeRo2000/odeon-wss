package com.romanpulov.odeonwss.dto;

public class MediaFileValidationDTOImpl implements MediaFileValidationDTO {
    private Long artifactId;
    private String artifactTitle;
    private Long artifactYear;
    private String artistName;
    private Long artifactSize;
    private Long artifactDuration;
    private Long trackNum;
    private String trackTitle;
    private String mediaFileName;
    private String mediaFileFormat;
    private Long mediaFileBitrate;
    private Long mediaFileSize;
    private Long mediaFileDuration;
    private Long mediaFileWidth;
    private Long mediaFileHeight;

    @Override
    public Long getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(Long artifactId) {
        this.artifactId = artifactId;
    }

    @Override
    public String getArtifactTitle() {
        return artifactTitle;
    }

    public void setArtifactTitle(String artifactTitle) {
        this.artifactTitle = artifactTitle;
    }

    @Override
    public Long getArtifactYear() {
        return artifactYear;
    }

    public void setArtifactYear(Long artifactYear) {
        this.artifactYear = artifactYear;
    }

    @Override
    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public Long getArtifactSize() {
        return artifactSize;
    }

    public void setArtifactSize(Long artifactSize) {
        this.artifactSize = artifactSize;
    }

    @Override
    public Long getArtifactDuration() {
        return artifactDuration;
    }

    public void setArtifactDuration(Long artifactDuration) {
        this.artifactDuration = artifactDuration;
    }

    @Override
    public Long getTrackNum() {
        return trackNum;
    }

    public void setTrackNum(Long trackNum) {
        this.trackNum = trackNum;
    }

    @Override
    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    @Override
    public String getMediaFileName() {
        return mediaFileName;
    }

    public void setMediaFileName(String mediaFileName) {
        this.mediaFileName = mediaFileName;
    }

    @Override
    public String getMediaFileFormat() {
        return mediaFileFormat;
    }

    public void setMediaFileFormat(String mediaFileFormat) {
        this.mediaFileFormat = mediaFileFormat;
    }

    @Override
    public Long getMediaFileBitrate() {
        return mediaFileBitrate;
    }

    public void setMediaFileBitrate(Long mediaFileBitrate) {
        this.mediaFileBitrate = mediaFileBitrate;
    }

    @Override
    public Long getMediaFileSize() {
        return mediaFileSize;
    }

    public void setMediaFileSize(Long mediaFileSize) {
        this.mediaFileSize = mediaFileSize;
    }

    @Override
    public Long getMediaFileDuration() {
        return mediaFileDuration;
    }

    public void setMediaFileDuration(Long mediaFileDuration) {
        this.mediaFileDuration = mediaFileDuration;
    }

    @Override
    public Long getMediaFileWidth() {
        return mediaFileWidth;
    }

    public void setMediaFileWidth(Long mediaFileWidth) {
        this.mediaFileWidth = mediaFileWidth;
    }

    @Override
    public Long getMediaFileHeight() {
        return mediaFileHeight;
    }

    public void setMediaFileHeight(Long mediaFileHeight) {
        this.mediaFileHeight = mediaFileHeight;
    }

    @Override
    public String toString() {
        return "MediaFileValidationDTOImpl{" +
                "artifactId=" + getArtifactId() +
                ", artifactTitle='" + getArtifactTitle() + '\'' +
                ", artifactYear=" + getArtifactYear() +
                ", artistName='" + getArtistName() + '\'' +
                ", artifactSize=" + getArtifactSize() +
                ", artifactDuration=" + getArtifactDuration() +
                ", trackNum=" + getTrackNum() +
                ", trackTitle='" + getTrackTitle() + '\'' +
                ", mediaFileName='" + getMediaFileName() + '\'' +
                ", mediaFileFormat='" + getMediaFileFormat() + '\'' +
                ", mediaFileBitrate=" + getMediaFileBitrate() +
                ", mediaFileSize=" + getMediaFileSize() +
                ", mediaFileDuration=" + getMediaFileDuration() +
                ", mediaFileWidth=" + getMediaFileWidth() +
                ", mediaFileHeight=" + getMediaFileHeight() +
                '}';
    }
}
