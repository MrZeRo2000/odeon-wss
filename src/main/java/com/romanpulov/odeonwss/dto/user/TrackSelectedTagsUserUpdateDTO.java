package com.romanpulov.odeonwss.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.dto.ArtifactDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackSelectedTagsUserUpdateDTO {
    private ArtifactDTO artifact;
    private List<Long> trackIds;
    private List<String> tags;

    public ArtifactDTO getArtifact() {
        return artifact;
    }

    public void setArtifact(ArtifactDTO artifact) {
        this.artifact = artifact;
    }

    public List<Long> getTrackIds() {
        return trackIds;
    }

    public void setTrackIds(List<Long> trackIds) {
        this.trackIds = trackIds;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "TrackSelectedTagsUserUpdateDTO{" +
                "artifact=" + artifact +
                ", trackIds=" + trackIds +
                ", tags=" + tags +
                '}';
    }
}
