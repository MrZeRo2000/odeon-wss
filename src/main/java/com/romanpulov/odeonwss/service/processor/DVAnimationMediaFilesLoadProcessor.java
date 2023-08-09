package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import org.springframework.stereotype.Component;

@Component
public class DVAnimationMediaFilesLoadProcessor extends AbstractDVNonMusicMediaFilesLoadProcessor {
    public DVAnimationMediaFilesLoadProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            MediaParser mediaParser)
    {
        super(
                artifactTypeRepository,
                artifactRepository,
                trackRepository,
                mediaFileRepository,
                mediaParser,
                ArtifactTypeRepository::getWithDVAnimation
        );
    }
}
