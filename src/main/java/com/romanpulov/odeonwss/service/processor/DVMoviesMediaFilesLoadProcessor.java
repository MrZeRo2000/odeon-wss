package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import org.springframework.stereotype.Component;

@Component
public class DVMoviesMediaFilesLoadProcessor extends AbstractDVNonMusicMediaFilesLoadProcessor {
    public DVMoviesMediaFilesLoadProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            MediaFileMapper mediaFileMapper,
            MediaParser mediaParser)
    {
        super(
                artifactTypeRepository,
                artifactRepository,
                trackRepository,
                mediaFileRepository,
                mediaFileMapper,
                mediaParser,
                ArtifactTypeRepository::getWithDVMovies
        );
    }
}
