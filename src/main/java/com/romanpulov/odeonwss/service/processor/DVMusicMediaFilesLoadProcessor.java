package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DVMusicMediaFilesLoadProcessor extends AbstractDVMediaFilesLoadProcessor {

    public DVMusicMediaFilesLoadProcessor(
            ArtifactRepository artifactRepository,
            CompositionRepository compositionRepository,
            MediaFileRepository mediaFileRepository,
            MediaParser mediaParser
    ) {
        super(
                ArtifactType.withDVMusic(),
                artifactRepository,
                compositionRepository,
                mediaFileRepository,
                mediaParser
        );
    }

    @Override
    protected void processArtifactSizeDuration(Map<Artifact, SizeDuration> artifactSizeDurationMap) {
        this.updateArtifactsDuration(artifactSizeDurationMap);
    }
}
