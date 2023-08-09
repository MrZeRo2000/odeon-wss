package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.TrackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DVMoviesValidateProcessor extends AbstractDVNonMusicValidateProcessor {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(DVMoviesValidateProcessor.class);

    public DVMoviesValidateProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository,
            TrackService trackService) {
        super(
                artifactTypeRepository,
                artifactRepository,
                mediaFileRepository,
                trackService,
                ArtifactTypeRepository::getWithDVMovies
        );
    }
}
