package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.MediaFile;

public class EntityCompositionBuilder extends AbstractClassBuilder<Composition> {

    public EntityCompositionBuilder() {
        super(Composition.class);
    }

    public EntityCompositionBuilder withArtifact(Artifact artifact) {
        instance.setArtifact(artifact);
        return this;
    }

    public EntityCompositionBuilder withArtist(Artist artist) {
        instance.setArtist(artist);
        return this;
    }

    public EntityCompositionBuilder withPerformerArtist(Artist performerArtist) {
        instance.setPerformerArtist(performerArtist);
        return this;
    }

    public EntityCompositionBuilder withMediaFile(MediaFile mediaFile) {
        instance.getMediaFiles().add(mediaFile);
        return this;
    }

    public EntityCompositionBuilder withTitle(String title) {
        instance.setTitle(title);
        return this;
    }

    public EntityCompositionBuilder withDuration(long duration) {
        instance.setDuration(duration);
        return this;
    }

    public EntityCompositionBuilder withDiskNum(long diskNum) {
        instance.setDiskNum(diskNum);
        return this;
    }

    public EntityCompositionBuilder withNum(long num) {
        instance.setNum(num);
        return this;
    }

    public EntityCompositionBuilder withMigrationId(long migrationId) {
        instance.setMigrationId(migrationId);
        return this;
    }
}
