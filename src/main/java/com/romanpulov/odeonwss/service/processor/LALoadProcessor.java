package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class LALoadProcessor extends AbstractArtistProcessor {

    public LALoadProcessor(ArtistRepository artistRepository, ArtifactRepository artifactRepository) {
        super(artistRepository, artifactRepository);
    }

    @Override
    protected void processCompositionsPath(Path path, Artifact artifact) throws ProcessorException {

    }
}
