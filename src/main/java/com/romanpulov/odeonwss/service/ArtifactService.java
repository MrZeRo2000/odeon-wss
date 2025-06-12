package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.ArtifactDTOImpl;
import com.romanpulov.odeonwss.dto.ArtifactTransformer;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.Tag;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.ArtifactMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArtifactService
        extends AbstractEntityService<Artifact, ArtifactDTO, ArtifactRepository>
        implements EditableObjectService<ArtifactDTO> {

    private final ArtifactTransformer artifactTransformer;
    private final TagRepository tagRepository;

    public ArtifactService(
            ArtifactRepository artifactRepository,
            ArtifactMapper artifactMapper,
            ArtifactTransformer artifactTransformer,
            ArtistRepository artistRepository,
            TagRepository tagRepository) {
        super(artifactRepository, artifactMapper);
        this.artifactTransformer = artifactTransformer;
        this.tagRepository = tagRepository;


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

        // ensure tags
        Set<Tag> tags = dto
                .getTags()
                .stream()
                .map(v -> tagRepository.findTagByName(v).orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(v);
                    tagRepository.save(newTag);
                    return newTag;
                }))
                .collect(Collectors.toSet());

        // perform changes
        artifact.setTags(tags);
        repository.save(artifact);

        List<ArtifactDTO> result = artifactTransformer.transform(repository.findAllFlatDTOTagsByArtifactId(artifact.getId()));
        return result.isEmpty() ? new ArtifactDTOImpl() : result.get(0);
    }
}
