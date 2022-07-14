package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.MediaFileEditDTO;
import com.romanpulov.odeonwss.dto.MediaFileTableDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.view.IdNameView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MediaFileService implements EditableObjectService <MediaFileEditDTO> {

    private final ArtifactRepository artifactRepository;

    private final MediaFileRepository mediaFileRepository;

    public MediaFileService(ArtifactRepository artifactRepository, MediaFileRepository mediaFileRepository) {
        this.artifactRepository = artifactRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    public List<MediaFileTableDTO> getTable(Long artifactId) throws CommonEntityNotFoundException {
        Optional<Artifact> existingArtifact = artifactRepository.findById(artifactId);
        if (existingArtifact.isPresent()) {
            return mediaFileRepository.getMediaFileTableByArtifact(existingArtifact.get());
        } else {
            throw new CommonEntityNotFoundException("Artifact", artifactId);
        }
    }

    public List<IdNameView> getTableIdName(Long artifactId) throws CommonEntityNotFoundException {
        Optional<Artifact> existingArtifact = artifactRepository.findById(artifactId);
        if (existingArtifact.isPresent()) {
            return mediaFileRepository.findByArtifactOrderByName(existingArtifact.get());
        } else {
            throw new CommonEntityNotFoundException("Artifact", artifactId);
        }
    }

    @Override
    public MediaFileEditDTO getById(Long id) throws CommonEntityNotFoundException {
        Optional<MediaFileEditDTO> existingDTO = mediaFileRepository.getMediaFileEditById(id);
        if (existingDTO.isPresent()) {
            return existingDTO.get();
        } else {
            throw new CommonEntityNotFoundException("MediaFile", id);
        }
    }

    @Override
    public MediaFileEditDTO insert(MediaFileEditDTO o) throws CommonEntityNotFoundException {
        MediaFile mediaFile = MediaFileMapper.fromMediaFileEditDTO(o);
        mediaFileRepository.save(mediaFile);
        return getById(mediaFile.getId());
    }

    @Override
    public MediaFileEditDTO update(MediaFileEditDTO o) throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        Optional<MediaFileEditDTO> existingDTO = mediaFileRepository.getMediaFileEditById(o.getId());
        if (existingDTO.isPresent()) {
            MediaFile mediaFile = MediaFileMapper.fromMediaFileEditDTO(o);
            mediaFileRepository.save(mediaFile);
            return getById(mediaFile.getId());
        } else {
            throw new CommonEntityNotFoundException("MediaFile", o.getId());
        }
    }

    @Override
    public void deleteById(Long id) throws CommonEntityNotFoundException {
        Optional<MediaFile> existingMediaFile = mediaFileRepository.findById(id);
        if (existingMediaFile.isPresent()) {
            mediaFileRepository.delete(existingMediaFile.get());
        } else {
            throw new CommonEntityNotFoundException("MediaFile", id);
        }
    }
}
