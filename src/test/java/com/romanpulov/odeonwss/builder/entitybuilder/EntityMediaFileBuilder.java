package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.MediaFile;

public class EntityMediaFileBuilder {
    private final MediaFile mediaFile;

    public EntityMediaFileBuilder() {
        mediaFile = new MediaFile();
    }

    public EntityMediaFileBuilder withArtifact(Artifact artifact) {
        mediaFile.setArtifact(artifact);
        return this;
    }

    public EntityMediaFileBuilder withName(String name) {
        mediaFile.setName(name);
        return this;
    }

    public EntityMediaFileBuilder withFormat(String format) {
        mediaFile.setFormat(format);
        return this;
    }

    public EntityMediaFileBuilder withSize(Long size) {
        mediaFile.setSize(size);
        return this;
    }

    public EntityMediaFileBuilder withBitrate(Long bitrate) {
        mediaFile.setBitrate(bitrate);
        return this;
    }

    public EntityMediaFileBuilder withDuration(Long duration) {
        mediaFile.setDuration(duration);
        return this;
    }

    public MediaFile build() {
        return mediaFile;
    }
}
