package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.TrackDTO;
import com.romanpulov.odeonwss.dto.TrackFlatDTO;
import com.romanpulov.odeonwss.dto.TrackTransformer;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.TrackMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrackService
        extends AbstractEntityService<Track, TrackDTO, TrackRepository>
        implements EditableObjectService<TrackDTO>{

    private final ArtifactRepository artifactRepository;

    private final TrackTransformer transformer;

    private final MediaFileRepository mediaFileRepository;

    public TrackService(
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            TrackMapper trackMapper,
            TrackTransformer transformer,
            MediaFileRepository mediaFileRepository,
            DVProductRepository dvProductRepository) {
        super(trackRepository, trackMapper);
        this.artifactRepository = artifactRepository;
        this.transformer = transformer;
        this.mediaFileRepository = mediaFileRepository;

        this.setOnBeforeSaveEntityHandler(entity -> {
            entity.setMediaFiles(
                entity
                        .getMediaFiles()
                        .stream()
                        .filter(v -> v.getId() != null)
                        .map(v -> mediaFileRepository.findById(v.getId()).orElse(null))
                        .filter(v -> !Objects.isNull(v))
                        .collect(Collectors.toSet())
            );
            entity.setDvProducts(
                    entity
                            .getDvProducts()
                            .stream()
                            .filter(v -> v.getId() != null)
                            .map(v -> dvProductRepository.findById(v.getId()).orElse(null))
                            .filter(v -> !Objects.isNull(v))
                            .collect(Collectors.toSet())
            );
        });
    }

    public List<TrackDTO>getTable(Long artifactId) throws CommonEntityNotFoundException {
        if (artifactRepository.existsById(artifactId)) {
            return transformer.transform(repository.findAllFlatDTOByArtifactId(artifactId));
        } else {
            throw new CommonEntityNotFoundException("Artifact", artifactId);
        }
    }

    public List<TrackDTO> getTableByArtifactTypeId(ArtistType artistType, Long artifactTypeId) {
        return transformer.transform(repository.findAllFlatDTOByArtifactTypeId(artistType, artifactTypeId));
    }

    public List<TrackDTO> getTableByProductId(Long productId) {
        return transformer.transform(repository.findAllFlatDTOByDvProductId(productId));
    }

    @Override
    @Transactional
    public TrackDTO getById(Long id) throws CommonEntityNotFoundException {
        List<TrackFlatDTO> flatDTOS = repository.findFlatDTOById(id);
        if (flatDTOS.isEmpty()) {
            throw new CommonEntityNotFoundException(this.entityName, id);
        } else {
            return transformer.transform(flatDTOS).get(0);
        }
    }

    @Transactional
    public void insertTrackWithMedia(Track track, MediaFile mediaFile) {
        if (mediaFile.getId() == null) {
            //get id from existing
            Optional<MediaFile> existingMediaFile = mediaFileRepository.findFirstByArtifactAndName(
                    mediaFile.getArtifact(), mediaFile.getName());
            if (existingMediaFile.isPresent()) {
                mediaFile.setId(existingMediaFile.get().getId());
                mediaFile.setInsertDateTime(existingMediaFile.get().getInsertDateTime());
            }

            //save
            mediaFileRepository.save(mediaFile);
        } else {
            mediaFile = mediaFileRepository.findById(mediaFile.getId()).orElseThrow();
        }
        track.setMediaFiles(Set.of(mediaFile));
        repository.save(track);
    }

    @Transactional
    public void insertTracksWithMedia(Iterable<Track> tracks, Iterable<MediaFile> mediaFiles) {
        mediaFileRepository.saveAll(mediaFiles);
        repository.saveAll(tracks);
    }
}
