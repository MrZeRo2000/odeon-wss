package com.romanpulov.odeonwss.dto;

public class MediaFileValidationDTOBuilder {
    private final MediaFileValidationDTOImpl instance;

    public MediaFileValidationDTOBuilder() {
        this.instance = new MediaFileValidationDTOImpl();
    }

    public MediaFileValidationDTOBuilder withArtifactId(long artifactId) {
        instance.setArtifactId(artifactId);
        return this;
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

    public MediaFileValidationDTOBuilder withArtifactSize(long artifactSize) {
        instance.setArtifactSize(artifactSize);
        return this;
    }

    public MediaFileValidationDTOBuilder withArtifactDuration(long artifactDuration) {
        instance.setArtifactDuration(artifactDuration);
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

    public MediaFileValidationDTOBuilder withMediaFileSize(long mediaFileSize) {
        instance.setMediaFileSize(mediaFileSize);
        return this;
    }

    public MediaFileValidationDTOBuilder withMediaFileDuration(long mediaFileDuration) {
        instance.setMediaFileDuration(mediaFileDuration);
        return this;
    }

    public MediaFileValidationDTO build() {
        return instance;
    }
}
