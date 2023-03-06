package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DVMusicMediaFilesLoadProcessor extends AbstractDVMediaFilesLoadProcessor {

    public DVMusicMediaFilesLoadProcessor(
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            MediaParser mediaParser
    ) {
        super(
                ArtifactType.withDVMusic(),
                artifactRepository,
                trackRepository,
                mediaFileRepository,
                mediaParser
        );
    }

    @Override
    protected void processArtifactSizeDuration(Map<Artifact, SizeDuration> artifactSizeDurationMap) {
        this.updateArtifactsDuration(artifactSizeDurationMap);
    }
}
