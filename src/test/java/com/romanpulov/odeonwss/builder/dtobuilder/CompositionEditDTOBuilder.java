package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;

public class CompositionEditDTOBuilder extends AbstractClassBuilder<CompositionEditDTO> {
    public CompositionEditDTOBuilder() {
        super(CompositionEditDTO.class);
    }

    public CompositionEditDTOBuilder withArtifact(Artifact artifact) {
        instance.setArtifactId(artifact.getId());
        return this;
    }

    public CompositionEditDTOBuilder withTitle(String title) {
        instance.setTitle(title);
        return this;
    }

    public CompositionEditDTOBuilder withDuration(long duration) {
        instance.setDuration(duration);
        return this;
    }

    public CompositionEditDTOBuilder withDiskNum(long diskNum) {
        instance.setDiskNum(diskNum);
        return this;
    }

    public CompositionEditDTOBuilder withNum(long num) {
        instance.setNum(num);
        return this;
    }

    public CompositionEditDTOBuilder withMediaName(String mediaName) {
        instance.setMediaName(mediaName);
        return this;
    }

    public CompositionEditDTOBuilder withMediaFormat(String mediaFormat) {
        instance.setMediaFormat(mediaFormat);
        return this;
    }

    public CompositionEditDTOBuilder withMediaSize(long mediaSize) {
        instance.setMediaSize(mediaSize);
        return this;
    }

    public CompositionEditDTOBuilder withMediaBitrate(long mediaBitrate) {
        instance.setMediaBitrate(mediaBitrate);
        return this;
    }

    public CompositionEditDTOBuilder withMediaDuration(long mediaDuration) {
        instance.setMediaDuration(mediaDuration);
        return this;
    }
}
