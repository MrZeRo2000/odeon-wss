package com.romanpulov.odeonwss.entitybuilder;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;

import java.time.LocalDate;

public class EntityArtifactBuilder {
    private final Artifact artifact;

    public EntityArtifactBuilder() {
        artifact = new Artifact();
    }

    public EntityArtifactBuilder withArtifactType(ArtifactType artifactType) {
        artifact.setArtifactType(artifactType);
        return this;
    }

    public EntityArtifactBuilder withArtist(Artist artist) {
        artifact.setArtist(artist);
        return this;
    }

    public EntityArtifactBuilder withTitle(String title) {
        artifact.setTitle(title);
        return this;
    }

    public EntityArtifactBuilder withYear(Long year) {
        artifact.setYear(year);
        return this;
    }

    public EntityArtifactBuilder withDuration(Long duration) {
        artifact.setDuration(duration);
        return this;
    }

    public EntityArtifactBuilder withInsertDate(LocalDate insertDate) {
        artifact.setInsertDate(insertDate);
        return this;
    }

    public Artifact build() {
        return artifact;
    }
}
