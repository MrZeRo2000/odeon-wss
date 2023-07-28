package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MediaFileService
        extends AbstractEntityService<MediaFile, MediaFileDTO, MediaFileRepository>
        implements EditableObjectService <MediaFileDTO> {

    private final ArtifactRepository artifactRepository;

    public MediaFileService(
            MediaFileRepository mediaFileRepository,
            MediaFileMapper mediaFileMapper,
            ArtifactRepository artifactRepository) {
        super(mediaFileRepository, mediaFileMapper);
        this.artifactRepository = artifactRepository;

        this.setOnBeforeSaveEntityHandler(entity -> {
           if (!artifactRepository.existsById(entity.getArtifact().getId())) {
               throw new CommonEntityNotFoundException("MediaFile", entity.getArtifact().getId());
           }
        });
    }

    public List<MediaFileDTO> getTable(Long artifactId) throws CommonEntityNotFoundException {
        Optional<Artifact> existingArtifact = artifactRepository.findById(artifactId);
        if (existingArtifact.isPresent()) {
            return repository.findAllDTOByArtifactId(artifactId);
        } else {
            throw new CommonEntityNotFoundException("Artifact", artifactId);
        }
    }

    public List<IdNameDTO> getTableIdName(Long artifactId) throws CommonEntityNotFoundException {
        Optional<Artifact> existingArtifact = artifactRepository.findById(artifactId);
        if (existingArtifact.isPresent()) {
            return repository.findByArtifactOrderByName(existingArtifact.get());
        } else {
            throw new CommonEntityNotFoundException("Artifact", artifactId);
        }
    }
}
