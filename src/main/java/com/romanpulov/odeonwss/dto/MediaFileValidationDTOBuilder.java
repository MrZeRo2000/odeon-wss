package com.romanpulov.odeonwss.dto;

public class MediaFileValidationDTOBuilder {
    private final MediaFileValidationDTOImpl instance;

    public MediaFileValidationDTOBuilder() {
        this.instance = new MediaFileValidationDTOImpl();
    }

    public MediaFileValidationDTOBuilder withArtifactTitle(String artifactTitle) {
        instance.setArtifactTitle(artifactTitle);
        return this;
    }

    public MediaFileValidationDTOBuilder withArtifactYear(long artifactYear) {
        instance.setArtifactYear(artifactYear);
        return this;
    }

    public MediaFileValidationDTOBuilder withArtistName(String artistName) {
        instance.setArtistName(artistName);
        return this;
    }

    public MediaFileValidationDTOBuilder withTrackNum(long trackNum) {
        instance.setTrackNum(trackNum);
        return this;
    }

    public MediaFileValidationDTOBuilder withTrackTitle(String trackTitle) {
        instance.setTrackTitle(trackTitle);
        return this;
    }

    public MediaFileValidationDTOBuilder withMediaFileName(String mediaFileName) {
        instance.setMediaFileName(mediaFileName);
        return this;
    }

    public MediaFileValidationDTOBuilder withMediaFileFormat(String mediaFileFormat) {
        instance.setMediaFileFormat(mediaFileFormat);
        return this;
    }

    public MediaFileValidationDTO build() {
        return instance;
    }
}
