package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;

public class CompositionEditDTOBuilder extends AbstractClassBuilder<CompositionEditDTO> {
    public CompositionEditDTOBuilder() {
        super(CompositionEditDTO.class);
    }

    public CompositionEditDTO withArtifact(Artifact artifact) {
        instance.setArtifactId(artifact.getId());
        return instance;
    }

    public CompositionEditDTO withTitle(String title) {
        instance.setTitle(title);
        return instance;
    }

    public CompositionEditDTO withDuration(long duration) {
        instance.setDuration(duration);
        return instance;
    }

    public CompositionEditDTO withDiskNum(long diskNum) {
        instance.setDiskNum(diskNum);
        return instance;
    }

    public CompositionEditDTO withNum(long num) {
        instance.setNum(num);
        return instance;
    }

    public CompositionEditDTO withMediaName(String mediaName) {
        instance.setMediaName(mediaName);
        return instance;
    }

    public CompositionEditDTO withMediaFormat(String mediaFormat) {
        instance.setMediaFormat(mediaFormat);
        return instance;
    }

    public CompositionEditDTO withMediaSize(long mediaSize) {
        instance.setMediaSize(mediaSize);
        return instance;
    }

    public CompositionEditDTO withMediaBitrate(long mediaBitrate) {
        instance.setMediaBitrate(mediaBitrate);
        return instance;
    }

    public CompositionEditDTO withMediaDuration(long mediaDuration) {
        instance.setMediaDuration(mediaDuration);
        return instance;
    }
}
