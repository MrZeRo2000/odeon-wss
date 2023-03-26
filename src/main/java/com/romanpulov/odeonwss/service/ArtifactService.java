package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.ArtifactMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.dto.ArtifactEditDTO;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class ArtifactService implements EditableObjectService<ArtifactEditDTO> {

    private final ArtifactRepository artifactRepository;

    public ArtifactService(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    @Override
    public ArtifactEditDTO getById(Long id) throws CommonEntityNotFoundException {
        return artifactRepository
                .findArtifactEditById(id)
                .map(ArtifactMapper::toEditDTO)
                .orElseThrow(() -> new CommonEntityNotFoundException("Artifact", id));
    }

    @Override
    @Transactional
    public ArtifactEditDTO insert(ArtifactEditDTO aed) throws CommonEntityNotFoundException {
        Artifact artifact = ArtifactMapper.createFromArtifactEditDTO(aed);
        artifactRepository.save(artifact);
        return getById(artifact.getId());
    }

    @Override
    @Transactional
    public ArtifactEditDTO update(ArtifactEditDTO aed) throws CommonEntityNotFoundException {
        Optional<Artifact> existingArtifact = artifactRepository.findById(aed.getId());
        if (existingArtifact.isPresent()) {
            Artifact artifact = ArtifactMapper.createFromArtifactEditDTO(aed);
            artifact.setInsertDate(existingArtifact.get().getInsertDate());
            artifactRepository.save(artifact);
            return getById(artifact.getId());
        } else {
            throw new CommonEntityNotFoundException("Artifact", aed.getId());
        }
    }

    @Override
    public void deleteById(Long id) throws CommonEntityNotFoundException {
        Optional<Artifact> existingArtifact = artifactRepository.findById(id);
        if (existingArtifact.isPresent()) {
            artifactRepository.delete(existingArtifact.get());
        } else {
            throw new CommonEntityNotFoundException("Artifact", id);
        }
    }
}
