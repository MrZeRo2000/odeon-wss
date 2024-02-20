package com.romanpulov.odeonwss.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.MediaFileDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackDurationsUserUpdateDTO {
    private ArtifactDTO artifact;
    private MediaFileDTO mediaFile;
    private List<String> chapters;

    public ArtifactDTO getArtifact() {
        return artifact;
    }

    public void setArtifact(ArtifactDTO artifact) {
        this.artifact = artifact;
    }

    public MediaFileDTO getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(MediaFileDTO mediaFile) {
        this.mediaFile = mediaFile;
    }

    public List<String> getChapters() {
        return chapters;
    }

    public void setChapters(List<String> chapters) {
        this.chapters = chapters;
    }

    @Override
    public String toString() {
        return "TrackDurationsUserUpdateDTO{" +
                "artifact=" + artifact +
                ", mediaFile=" + mediaFile +
                ", chapters=" + chapters +
                '}';
    }
}
