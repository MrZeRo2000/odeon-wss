package com.romanpulov.odeonwss.dto;

import com.romanpulov.odeonwss.service.processor.NamesParser;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MediaFileValidationDTO extends CompositionValidationDTO {
    private static final String FORMAT_DELIMITER = " >> ";

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

    public MediaFileValidationDTO(String artistName, String artifactTitle, Long artifactYear, Long compositionNum, String compositionTitle, String mediaFileName, String mediaFileFormat) {
        super(artistName, artifactTitle, artifactYear, compositionNum, compositionTitle);
        this.mediaFileName = mediaFileName;
        this.mediaFileFormat = mediaFileFormat;
    }

    public MediaFileValidationDTO(String artistName, String artifactTitle, Long artifactYear, String mediaFileName, String mediaFileFormat) {
        super(artistName, artifactTitle, artifactYear, null, null);
        this.mediaFileName = mediaFileName;
        this.mediaFileFormat = mediaFileFormat;
    }

    public MediaFileValidationDTO() {}

    public static Set<String> getMediaFiles(List<MediaFileValidationDTO> data) {
        return data.stream()
                .map(d ->
                        d.getArtistName() +
                                FORMAT_DELIMITER +
                                d.getArtifactYear() + " " + d.getArtifactTitle() +
                                FORMAT_DELIMITER +
                                d.getMediaFileName())
                .collect(Collectors.toSet());
    }

    public static Set<String> getCompositions(List<MediaFileValidationDTO> data) {
        return data.stream()
                .map(d ->
                d.getArtistName() +
                        FORMAT_DELIMITER +
                        NamesParser.formatMusicArtifact(d.getArtifactYear(), d.getArtifactTitle()) +
                        FORMAT_DELIMITER +
                        NamesParser.formatMusicCompositionWithFile(d.getCompositionNum(), d.getCompositionTitle(), d.getMediaFileName()))
                .collect(Collectors.toSet());
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
                "artistName='" + getArtistName() + '\'' +
                ", artifactTitle='" + getArtifactTitle() + '\'' +
                ", artifactYear=" + getArtifactYear() +
                ", compositionNum=" + getCompositionNum() +
                ", compositionTitle='" + getCompositionTitle() + '\'' +
                ", mediaFileName='" + mediaFileName + '\'' +
                ", mediaFileFormat='" + mediaFileFormat + '\'' +
                '}';
    }
}
