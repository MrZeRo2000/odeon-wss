package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.dto.CompositionTableDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.CompositionMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CompositionService implements EditableObjectService<CompositionEditDTO>{

    private final ArtifactRepository artifactRepository;

    private final CompositionRepository compositionRepository;

    private final MediaFileRepository mediaFileRepository;

    public CompositionService(ArtifactRepository artifactRepository, CompositionRepository compositionRepository, MediaFileRepository mediaFileRepository) {
        this.artifactRepository = artifactRepository;
        this.compositionRepository = compositionRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    public List<CompositionTableDTO>getTable(Long artifactId) throws CommonEntityNotFoundException {
        Optional<Artifact> existingArtifact = artifactRepository.findById(artifactId);
        if (existingArtifact.isPresent()) {
            return compositionRepository.getCompositionTableByArtifact(existingArtifact.get());
        } else {
            throw new CommonEntityNotFoundException("Artifact", artifactId);
        }
    }

    @Override
    @Transactional
    public CompositionEditDTO getById(Long id) throws CommonEntityNotFoundException {
        Optional<Composition> existingComposition = compositionRepository.findById(id);
        if (existingComposition.isPresent()) {
            return CompositionMapper.toEditDTO(existingComposition.get());
        } else {
            throw new CommonEntityNotFoundException("Composition", id);
        }
    }

    @Override
    @Transactional
    public CompositionEditDTO insert(CompositionEditDTO o) throws CommonEntityNotFoundException {
        Artifact artifact = artifactRepository.findById(o.getArtifactId())
                .orElseThrow(() -> new CommonEntityNotFoundException("Artifact", o.getArtifactId()));
        Composition composition = CompositionMapper.createFromEditDTO(o, artifact);

        compositionRepository.save(composition);

        return getById(composition.getId());
    }

    @Override
    @Transactional
    public CompositionEditDTO update(CompositionEditDTO o) throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        Artifact artifact = artifactRepository.findById(o.getArtifactId())
                .orElseThrow(() -> new CommonEntityNotFoundException("Artifact", o.getArtifactId()));
        Composition composition = compositionRepository.findById(o.getId())
                .orElseThrow(() -> new CommonEntityNotFoundException("Composition", o.getId()));
        Set<MediaFile> mediaFiles = StreamSupport.stream(mediaFileRepository.findAllById(o.getMediaFileIds()).spliterator(), false).collect(Collectors.toSet());

        CompositionMapper.updateFromEditDTO(composition, o, artifact, mediaFiles);

        compositionRepository.save(composition);

        return getById(o.getId());
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws CommonEntityNotFoundException {
        Optional<Composition> existingComposition = compositionRepository.findById(id);
        if (existingComposition.isPresent()) {
            compositionRepository.delete(existingComposition.get());
        } else {
            throw new CommonEntityNotFoundException("Composition", id);
        }
    }

    @Transactional
    public void insertCompositionWithMedia(Composition composition, MediaFile mediaFile) {
        if (mediaFile.getId() == null) {
            mediaFileRepository.save(mediaFile);
        } else {
            mediaFile = mediaFileRepository.findById(mediaFile.getId()).orElseThrow();
        }
        composition.setMediaFiles(Set.of(mediaFile));
        compositionRepository.save(composition);
    }

    @Transactional
    public void insertCompositionsWithMedia(Iterable<Composition> compositions, Iterable<MediaFile> mediaFiles) {
        mediaFileRepository.saveAll(mediaFiles);
        compositionRepository.saveAll(compositions);
    }
}
