package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.springframework.stereotype.Component;

@Component
public class DVMoviesMediaFilesLoadProcessor extends AbstractDVMediaFilesLoadProcessor {
    public DVMoviesMediaFilesLoadProcessor(
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository,
            MediaParser mediaParser)
    {
        super(
                ArtifactType.withDVMovies(),
                artifactRepository,
                mediaFileRepository,
                mediaParser
        );
    }
}
