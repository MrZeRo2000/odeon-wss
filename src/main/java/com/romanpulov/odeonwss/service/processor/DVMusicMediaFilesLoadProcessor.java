package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.springframework.stereotype.Component;

@Component
public class DVMusicMediaFilesLoadProcessor extends AbstractDVMediaFilesLoadProcessor {

    public DVMusicMediaFilesLoadProcessor(
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository,
            MediaParser mediaParser
    ) {
        super(
                ArtifactType.withDVMusic(),
                artifactRepository,
                mediaFileRepository,
                mediaParser
        );
    }

}
