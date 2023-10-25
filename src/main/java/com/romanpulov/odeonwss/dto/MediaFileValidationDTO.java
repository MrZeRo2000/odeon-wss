package com.romanpulov.odeonwss.dto;

public interface MediaFileValidationDTO {
    String getArtifactTitle();
    Long getArtifactYear();
    String getArtistName();
    Long getArtifactSize();
    Long getArtifactDuration();
    Long getTrackNum();
    String getTrackTitle();
    String getMediaFileName();
    String getMediaFileFormat();
    Long getMediaFileBitrate();
    Long getMediaFileSize();
    Long getMediaFileDuration();
}
