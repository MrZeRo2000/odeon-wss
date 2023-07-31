package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.MediaFileDTOImpl;

public class MediaFileDTOBuilder extends AbstractClassBuilder<MediaFileDTOImpl> {
    public MediaFileDTOBuilder() {
        super(MediaFileDTOImpl.class);
    }

    public MediaFileDTOBuilder withId(long id) {
        this.instance.setId(id);
        return this;
    }

    public MediaFileDTOBuilder withArtifactId(long artifactId) {
        this.instance.setArtifactId(artifactId);
        return this;
    }

    public MediaFileDTOBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }

    public MediaFileDTOBuilder withFormat(String format) {
        this.instance.setFormat(format);
        return this;
    }

    public MediaFileDTOBuilder withSize(long size) {
        this.instance.setSize(size);
        return this;
    }

    public MediaFileDTOBuilder withBitrate(long bitrate) {
        this.instance.setBitrate(bitrate);
        return this;
    }

    public MediaFileDTOBuilder withDuration(long duration) {
        this.instance.setDuration(duration);
        return this;
    }
}
