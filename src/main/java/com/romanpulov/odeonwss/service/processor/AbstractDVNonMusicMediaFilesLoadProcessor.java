package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.vo.SizeDuration;

import java.util.Map;
import java.util.function.Function;

public abstract class AbstractDVNonMusicMediaFilesLoadProcessor extends AbstractDVMediaFilesLoadProcessor {
    public AbstractDVNonMusicMediaFilesLoadProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            MediaFileMapper mediaFileMapper,
            MediaParser mediaParser,
            Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier)
    {
        super(
                artifactTypeRepository,
                artifactRepository,
                trackRepository,
                mediaFileRepository,
                mediaFileMapper,
                mediaParser,
                artifactTypeSupplier
        );
    }

    @Override
    protected void processArtifactSizeDuration(Map<Artifact, SizeDuration> artifactSizeDurationMap) {
        this.updateArtifactsDuration(artifactSizeDurationMap);
        this.updateTracksDuration(artifactSizeDurationMap);
    }
}
