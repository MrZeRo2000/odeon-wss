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
public class DVMoviesMediaFilesLoadProcessor extends AbstractDVMediaFilesLoadProcessor {
    public DVMoviesMediaFilesLoadProcessor(
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            MediaParser mediaParser)
    {
        super(
                ArtifactType.withDVMovies(),
                artifactRepository,
                trackRepository,
                mediaFileRepository,
                mediaParser
        );
    }

    @Override
    protected void processArtifactSizeDuration(Map<Artifact, AbstractDVMediaFilesLoadProcessor.SizeDuration> artifactSizeDurationMap) {
        this.updateArtifactsDuration(artifactSizeDurationMap);
        this.updateTracksDuration(artifactSizeDurationMap);
    }
}
