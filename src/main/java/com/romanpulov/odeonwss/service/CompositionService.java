package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.CompositionMapper;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    @Override
    public CompositionEditDTO getById(Long id) throws CommonEntityNotFoundException {
        Optional<CompositionEditDTO> existingDTO = compositionRepository.getCompositionEditById(id);
        if (existingDTO.isPresent()) {
            return existingDTO.get();
        } else {
            throw new CommonEntityNotFoundException("Composition", id);
        }
    }

    private Set<MediaFile> getOrCreateMediaFiles(CompositionEditDTO o) {
        Set<MediaFile> mediaFiles = new HashSet<>();

        if (!o.isMediaEmpty()) {
            MediaFile mediaFile = MediaFileMapper.fromCompositionEditDTO(o);
            if (mediaFileRepository.findFirstByArtifactAndName(mediaFile.getArtifact(), mediaFile.getName()).isEmpty()) {
                mediaFileRepository.save(mediaFile);
                mediaFiles.add(mediaFile);
            }
        }

        return mediaFiles;
    }

    @Override
    @Transactional
    public CompositionEditDTO insert(CompositionEditDTO o) throws CommonEntityNotFoundException {
        Artifact artifact = artifactRepository.findById(o.getArtifactId())
                .orElseThrow(() -> new CommonEntityNotFoundException("Artifact", o.getArtifactId()));

        Composition composition = CompositionMapper.fromEditDTO(o, artifact);
        composition.setMediaFiles(getOrCreateMediaFiles(o));
        compositionRepository.save(composition);

        return getById(composition.getId());
    }

    @Override
    @Transactional
    public CompositionEditDTO update(CompositionEditDTO o) throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        Artifact artifact = artifactRepository.findById(o.getArtifactId())
                .orElseThrow(() -> new CommonEntityNotFoundException("Artifact", o.getArtifactId()));
        compositionRepository.findById(o.getId())
                .orElseThrow(() -> new CommonEntityNotFoundException("Composition", o.getId()));

        Composition composition = CompositionMapper.fromEditDTO(o, artifact);
        composition.setMediaFiles(getOrCreateMediaFiles(o));
        compositionRepository.save(composition);

        /*
        TODO Rework

        Optional<MediaFile> existingMediaFile = mediaFileRepository.findFirstByCompositions(Set.of(composition));
        if (existingMediaFile.isPresent()) {
            if ((o.getMediaName() != null) && !o.getMediaName().isBlank()) {
                mediaFileRepository.save(MediaFileMapper.fromCompositionEditDTO(o, existingMediaFile.get(), composition));
            } else {
                mediaFileRepository.delete(existingMediaFile.get());
            }
        } else {
            if ((o.getMediaName() != null) && !o.getMediaName().isBlank()) {
                mediaFileRepository.save(MediaFileMapper.fromCompositionEditDTO(o, composition));
            }
        }

         */

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
}
