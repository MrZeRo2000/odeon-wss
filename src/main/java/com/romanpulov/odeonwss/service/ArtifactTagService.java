package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.ArtifactTagDTO;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.ArtifactTagMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTagRepository;
import org.springframework.stereotype.Service;

@Service
public class ArtifactTagService {
    private final ArtifactRepository artifactRepository;
    private final ArtifactTagRepository artifactTagRepository;
    private final ArtifactTagMapper artifactTagMapper;

    public ArtifactTagService(
            ArtifactRepository artifactRepository,
            ArtifactTagRepository artifactTagRepository,
            ArtifactTagMapper artifactTagMapper) {
        this.artifactRepository = artifactRepository;
        this.artifactTagRepository = artifactTagRepository;
        this.artifactTagMapper = artifactTagMapper;
    }

    /*

    public ArtifactTagDTO getByArtifactId(Long artifactId) throws CommonEntityNotFoundException {

    }

    public ArtifactTagDTO update(Long artifactId, ArtifactTagDTO artifactTagDTO)
      throws CommonEntityNotFoundException {

    }

     */
}
