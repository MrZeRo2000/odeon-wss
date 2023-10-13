package com.romanpulov.odeonwss.dto;

public interface MediaFileValidationDTO {
    String getArtifactTitle();
    Long getArtifactYear();
    String getArtistName();
    Long getTrackNum();
    String getTrackTitle();
    String getMediaFileName();
    String getMediaFileFormat();
    Long getMediaFileBitrate();
}
