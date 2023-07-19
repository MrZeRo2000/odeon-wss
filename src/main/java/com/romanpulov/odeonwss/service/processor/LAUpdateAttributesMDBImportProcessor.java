package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import org.springframework.stereotype.Component;

@Component
public class LAUpdateAttributesMDBImportProcessor extends AbstractMusicAttributesMDBImportProcessor {
    public LAUpdateAttributesMDBImportProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository) {
        super(MDBConst.LACONT_TABLE_NAME, artifactTypeRepository, artifactRepository, ArtifactTypeRepository::getWithLA);
    }
}
