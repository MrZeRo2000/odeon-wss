package com.romanpulov.odeonwss.dto;

public class MediaFileValidationDTOBuilder {
    private final MediaFileValidationDTO mediaFileValidationDTO;

    public MediaFileValidationDTOBuilder() {
        this.mediaFileValidationDTO = new MediaFileValidationDTO();
    }

    public MediaFileValidationDTOBuilder withArtistName(String artistName) {
        mediaFileValidationDTO.setArtistName(artistName);
        return this;
    }

    public MediaFileValidationDTOBuilder withArtifactTitle(String artifactTitle) {
        mediaFileValidationDTO.setArtifactTitle(artifactTitle);
        return this;
    }

    public MediaFileValidationDTOBuilder withArtifactYear(long artifactYear) {
        mediaFileValidationDTO.setArtifactYear(artifactYear);
        return this;
    }

    public MediaFileValidationDTO build() {
        return mediaFileValidationDTO;
    }
}
