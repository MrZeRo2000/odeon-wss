package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.MediaFile;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

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

    public CompositionEditDTOBuilder withMediaFileIds(Collection<Long> mediaFileIds) {
        instance.setMediaFiles(new HashSet<>(mediaFileIds));
        return this;
    }
}
