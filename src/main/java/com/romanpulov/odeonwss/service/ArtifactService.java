package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.ArtifactDTOImpl;
import com.romanpulov.odeonwss.dto.ArtifactTransformer;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactTag;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.ArtifactMapper;
import com.romanpulov.odeonwss.mapper.ArtifactTagMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTagRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Collection;
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

    public List<ArtifactDTO> getTableByOptional(List<Long> artifactTypeIds, List<Long> artistIds) {
        return this.artifactTransformer.transform(
                repository.findAllFlatDTOByOptional(
                        artifactTypeIds == null ? 0 : (long)artifactTypeIds.size(),
                        artifactTypeIds,
                        artistIds == null ? 0 : (long)artistIds.size(),
                        artistIds));
    }

    public ArtifactDTO updateTags(ArtifactDTO dto) throws CommonEntityNotFoundException {
        Artifact artifact = repository.findById(dto.getId()).orElseThrow(
                () -> new CommonEntityNotFoundException("Artifact", dto.getId()));
        List<ArtifactTag> oldArtifactTags = artifactTagRepository.findByArtifactId(artifact.getId());
        List<ArtifactTag> newArtifactTags = artifactTagMapper.createFromArtifactDTO(artifact, dto);

        Pair<Collection<ArtifactTag>, Collection<ArtifactTag>> mergedTags =
                artifactTagMapper.mergeTags(oldArtifactTags, newArtifactTags);

        // handle deleted
        artifactTagRepository.deleteAll(mergedTags.getSecond());

        // handle inserted
        artifactTagRepository.saveAll(mergedTags.getFirst());

        List<ArtifactDTO> result = artifactTransformer.transform(repository.findAllFlatDTOTagsByArtifactId(artifact.getId()));
        return result.isEmpty() ? new ArtifactDTOImpl() : result.get(0);
    }
}
