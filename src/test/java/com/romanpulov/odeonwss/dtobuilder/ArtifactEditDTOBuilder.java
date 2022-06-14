package com.romanpulov.odeonwss.dtobuilder;

import com.romanpulov.odeonwss.dto.ArtifactEditDTO;

public class ArtifactEditDTOBuilder {
    private final ArtifactEditDTO artifactEditDTO;

    public ArtifactEditDTOBuilder() {
        artifactEditDTO = new ArtifactEditDTO();
    }

    public ArtifactEditDTOBuilder withId(long id) {
        artifactEditDTO.setId(id);
        return this;
    }

    public ArtifactEditDTOBuilder withArtifactTypeId(long id) {
        artifactEditDTO.setArtifactTypeId(id);
        return this;
    }

    public ArtifactEditDTOBuilder withArtistId(long id) {
        artifactEditDTO.setArtistId(id);
        return this;
    }

    public ArtifactEditDTOBuilder withTitle(String title) {
        artifactEditDTO.setTitle(title);
        return this;
    }

    public ArtifactEditDTOBuilder withYear(long year) {
        artifactEditDTO.setYear(year);
        return this;
    }

    public ArtifactEditDTOBuilder withDuration(long duration) {
        artifactEditDTO.setDuration(duration);
        return this;
    }

    public ArtifactEditDTOBuilder withSize(long size) {
        artifactEditDTO.setSize(size);
        return this;
    }

    public ArtifactEditDTO build() {
        return artifactEditDTO;
    }

}
