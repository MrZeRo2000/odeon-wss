package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.entity.MediaFile;

public class EntityMediaFileBuilder extends AbstractClassBuilder<MediaFile> {

    public EntityMediaFileBuilder() {
        super(MediaFile.class);
    }

    public EntityMediaFileBuilder withArtifact(Artifact artifact) {
        this.instance.setArtifact(artifact);
        return this;
    }

    public EntityMediaFileBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }

    public EntityMediaFileBuilder withFormat(String format) {
        this.instance.setFormat(format);
        return this;
    }

    public EntityMediaFileBuilder withSize(Long size) {
        this.instance.setSize(size);
        return this;
    }

    public EntityMediaFileBuilder withBitrate(Long bitrate) {
        this.instance.setBitrate(bitrate);
        return this;
    }

    public EntityMediaFileBuilder withDuration(Long duration) {
        this.instance.setDuration(duration);
        return this;
    }

    public EntityMediaFileBuilder withMigrationId(long migrationId) {
        this.instance.setMigrationId(migrationId);
        return this;
    }
}
