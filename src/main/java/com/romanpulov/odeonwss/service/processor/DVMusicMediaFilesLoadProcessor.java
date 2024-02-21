package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.vo.SizeDuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DVMusicMediaFilesLoadProcessor extends AbstractDVMediaFilesLoadProcessor {

    public DVMusicMediaFilesLoadProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            MediaFileMapper mediaFileMapper,
            MediaParser mediaParser
    ) {
        super(
                artifactTypeRepository,
                artifactRepository,
                trackRepository,
                mediaFileRepository,
                mediaFileMapper,
                mediaParser,
                ArtifactTypeRepository::getWithDVMusic
        );
    }

    @Override
    protected void processArtifactSizeDuration(Map<Artifact, SizeDuration> artifactSizeDurationMap) {
        this.updateArtifactsDuration(artifactSizeDurationMap);
    }
}
