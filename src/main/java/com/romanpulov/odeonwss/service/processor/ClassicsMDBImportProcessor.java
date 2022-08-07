package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClassicsMDBImportProcessor extends AbstractMDBImportProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ClassicsMDBImportProcessor.class);

    private final ArtistRepository artistRepository;

    private final ArtifactRepository artifactRepository;

    private final CompositionRepository compositionRepository;

    public ClassicsMDBImportProcessor(ArtistRepository artistRepository, ArtifactRepository artifactRepository, CompositionRepository compositionRepository) {
        this.artistRepository = artistRepository;
        this.artifactRepository = artifactRepository;
        this.compositionRepository = compositionRepository;
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {

    }
}
