package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactTag;

public class EntityArtifactTagBuilder extends AbstractClassBuilder<ArtifactTag> {
    public EntityArtifactTagBuilder() {
        super(ArtifactTag.class);
    }

    public EntityArtifactTagBuilder withArtifact(Artifact artifact) {
        this.instance.setArtifact(artifact);
        return this;
    }

    public EntityArtifactTagBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }
}
