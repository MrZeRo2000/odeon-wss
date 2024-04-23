package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.ArtifactTransformer;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.ArtifactMapper;
import com.romanpulov.odeonwss.mapper.ArtifactTagMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTagRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtifactService
        extends AbstractEntityService<Artifact, ArtifactDTO, ArtifactRepository>
        implements EditableObjectService<ArtifactDTO> {

    private final ArtifactTransformer artifactTransformer;
    private final ArtifactTagRepository artifactTagRepository;
    private final ArtifactTagMapper artifactTagMapper;

    public ArtifactService(
            ArtifactRepository artifactRepository,
            ArtifactMapper artifactMapper,
            ArtifactTransformer artifactTransformer,
            ArtistRepository artistRepository,
            ArtifactTagRepository artifactTagRepository,
            ArtifactTagMapper artifactTagMapper) {
        super(artifactRepository, artifactMapper);
        this.artifactTransformer = artifactTransformer;
        this.artifactTagRepository = artifactTagRepository;
        this.artifactTagMapper = artifactTagMapper;

        this.setOnBeforeSaveEntityHandler(entity -> {
            if ((entity.getArtist() != null)
                    && (entity.getArtist().getId() != null)
                    && (!artistRepository.existsById(entity.getArtist().getId()))
            ) {
                throw new CommonEntityNotFoundException("Artist", entity.getArtist().getId());
            }

            if ((entity.getPerformerArtist() != null)
                    && (entity.getPerformerArtist().getId() != null)
                    && (!artistRepository.existsById(entity.getPerformerArtist().getId()))
            ) {
                throw new CommonEntityNotFoundException("Artist", entity.getPerformerArtist().getId());
            }
        });
    }

    @Transactional
    @Override
    public ArtifactDTO insert(ArtifactDTO dto) throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        ArtifactDTO result = super.insert(dto);

        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            artifactTagRepository.saveAll(artifactTagMapper.createFromArtifactDTO(repository.findById(result.getId()).orElseThrow(), dto));
        }

        return result;
    }

    @Override
    public ArtifactDTO getById(Long id) throws CommonEntityNotFoundException {
        return repository
                .findFlatDTOById(id)
                .map(this.artifactTransformer::transformOne)
                .orElseThrow(() -> new CommonEntityNotFoundException("Artifact", id));
    }

    public List<ArtifactDTO> getTable(ArtistType artistType, List<Long> artifactTypeIds) {
        return this.artifactTransformer.transform(
                repository.findAllFlatDTOByArtistTypeAndArtifactTypeIds(artistType, artifactTypeIds));
    }
}
