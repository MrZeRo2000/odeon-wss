package com.romanpulov.odeonwss.dto;

public class MediaFileValidationDTOImpl implements MediaFileValidationDTO {

    private String artifactTitle;

    public String getArtifactTitle() {
        return artifactTitle;
    }

    public void setArtifactTitle(String artifactTitle) {
        this.artifactTitle = artifactTitle;
    }

    private Long artifactYear;

    public Long getArtifactYear() {
        return artifactYear;
    }

    public void setArtifactYear(Long artifactYear) {
        this.artifactYear = artifactYear;
    }

    private String artistName;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    private Long artifactSize;

    @Override
    public Long getArtifactSize() {
        return artifactSize;
    }

    public void setArtifactSize(Long artifactSize) {
        this.artifactSize = artifactSize;
    }

    private Long artifactDuration;

    @Override
    public Long getArtifactDuration() {
        return artifactDuration;
    }

    public void setArtifactDuration(Long artifactDuration) {
        this.artifactDuration = artifactDuration;
    }

    private Long trackNum;

    public Long getTrackNum() {
        return trackNum;
    }

    public void setTrackNum(Long trackNum) {
        this.trackNum = trackNum;
    }

    private String trackTitle;

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    private String mediaFileName;

    public String getMediaFileName() {
        return mediaFileName;
    }

    public void setMediaFileName(String mediaFileName) {
        this.mediaFileName = mediaFileName;
    }

    private String mediaFileFormat;

    public String getMediaFileFormat() {
        return mediaFileFormat;
    }

    public void setMediaFileFormat(String mediaFileFormat) {
        this.mediaFileFormat = mediaFileFormat;
    }

    private Long mediaFileBitrate;

    @Override
    public Long getMediaFileBitrate() {
        return mediaFileBitrate;
    }

    public void setMediaFileBitrate(Long mediaFileBitrate) {
        this.mediaFileBitrate = mediaFileBitrate;
    }

    private Long mediaFileSize;

    @Override
    public Long getMediaFileSize() {
        return mediaFileSize;
    }

    public void setMediaFileSize(Long mediaFileSize) {
        this.mediaFileSize = mediaFileSize;
    }

    private Long mediaFileDuration;

    @Override
    public Long getMediaFileDuration() {
        return mediaFileDuration;
    }

    public void setMediaFileDuration(Long mediaFileDuration) {
        this.mediaFileDuration = mediaFileDuration;
    }

    @Override
    public String toString() {
        return "MediaFileValidationDTOImpl{" +
                "artifactTitle='" + artifactTitle + '\'' +
                ", artifactYear=" + artifactYear +
                ", artistName='" + artistName + '\'' +
                ", artifactSize=" + artifactSize +
                ", artifactDuration=" + artifactDuration +
                ", trackNum=" + trackNum +
                ", trackTitle='" + trackTitle + '\'' +
                ", mediaFileName='" + mediaFileName + '\'' +
                ", mediaFileFormat='" + mediaFileFormat + '\'' +
                ", mediaFileBitrate=" + mediaFileBitrate +
                ", mediaFileSize=" + mediaFileSize +
                ", mediaFileDuration=" + mediaFileDuration +
                '}';
    }
}
