package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

public class EntityArtifactBuilder extends AbstractClassBuilder<Artifact> {

    public EntityArtifactBuilder() {
        super(Artifact.class);
    }

    public EntityArtifactBuilder withId(Long id) {
        this.instance.setId(id);
        return this;
    }

    public EntityArtifactBuilder withArtifactType(ArtifactType artifactType) {
        this.instance.setArtifactType(artifactType);
        return this;
    }

    public EntityArtifactBuilder withArtist(Artist artist) {
        this.instance.setArtist(artist);
        return this;
    }

    public EntityArtifactBuilder withPerformerArtist(Artist performerArtist) {
        this.instance.setPerformerArtist(performerArtist);
        return this;
    }

    public EntityArtifactBuilder withTitle(String title) {
        this.instance.setTitle(title);
        return this;
    }

    public EntityArtifactBuilder withYear(Long year) {
        this.instance.setYear(year);
        return this;
    }

    public EntityArtifactBuilder withDuration(Long duration) {
        this.instance.setDuration(duration);
        return this;
    }

    public EntityArtifactBuilder withInsertDate(LocalDateTime insertDateTime) {
        this.instance.setInsertDateTime(insertDateTime);
        return this;
    }

    public EntityArtifactBuilder withMigrationId(long id) {
        this.instance.setMigrationId(id);
        return this;
    }

    public EntityArtifactBuilder withTags(Collection<Tag> tags) {
        this.instance.setTags(new HashSet<>(tags));
        return this;
    }
}
