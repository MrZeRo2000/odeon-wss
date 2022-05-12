package com.romanpulov.odeonwss.dto;

import java.util.Objects;

public class MediaFileValidationDTO extends CompositionValidationDTO {
    private final String mediaFileName;

    public String getMediaFileName() {
        return mediaFileName;
    }

    private final String mediaFileFormat;

    public String getMediaFileFormat() {
        return mediaFileFormat;
    }

    public MediaFileValidationDTO(String artistName, String artifactTitle, Long artifactYear, Long compositionNum, String compositionTitle, String mediaFileName, String mediaFileFormat) {
        super(artistName, artifactTitle, artifactYear, compositionNum, compositionTitle);
        this.mediaFileName = mediaFileName;
        this.mediaFileFormat = mediaFileFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MediaFileValidationDTO that = (MediaFileValidationDTO) o;
        return mediaFileName.equals(that.mediaFileName) && mediaFileFormat.equals(that.mediaFileFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mediaFileName, mediaFileFormat);
    }

    @Override
    public String toString() {
        return "MediaFileValidationDTO{" +
                "mediaFileName='" + mediaFileName + '\'' +
                ", mediaFileFormat='" + mediaFileFormat + '\'' +
                '}';
    }
}
