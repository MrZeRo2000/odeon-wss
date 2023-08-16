package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.DVProductService;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DVAnimationLoadProcessor extends AbstractDVNonMusicLoadProcessor {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(DVAnimationLoadProcessor.class);

    public DVAnimationLoadProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            MediaFileMapper mediaFileMapper,
            DVTypeRepository dvTypeRepository,
            DVProductService dvProductService,
            MediaParser mediaParser) {
        super(
                artifactTypeRepository,
                artifactRepository,
                trackRepository,
                mediaFileRepository,
                mediaFileMapper,
                dvTypeRepository,
                dvProductService,
                mediaParser,
                ArtifactTypeRepository::getWithDVAnimation);
    }
}
