package com.romanpulov.odeonwss.dto;

import java.util.List;

public class ArtifactTagDTOImpl implements ArtifactTagDTO {
    Long artifactId;
    List<String> names;

    public Long getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(Long artifactId) {
        this.artifactId = artifactId;
    }

    @Override
    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    @Override
    public String toString() {
        return "ArtifactTagDTOImpl{" +
                "artifactId=" + getArtifactId() +
                ", names=" + getNames() +
                '}';
    }
}
