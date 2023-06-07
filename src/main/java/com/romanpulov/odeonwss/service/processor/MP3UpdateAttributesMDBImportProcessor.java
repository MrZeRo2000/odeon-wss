package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import org.springframework.stereotype.Component;

@Component
public class MP3UpdateAttributesMDBImportProcessor extends  AbstractMusicAttributesMDBImportProcessor {
    public MP3UpdateAttributesMDBImportProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository) {
        super(MDBConst.MP3CDCONT_TABLE_NAME, artifactTypeRepository.getWithMP3(), artifactRepository);
    }
}
