package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.RowsAffectedDTO;
import com.romanpulov.odeonwss.dto.TrackDTO;
import com.romanpulov.odeonwss.dto.TrackFlatDTO;
import com.romanpulov.odeonwss.dto.TrackTransformer;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.TrackMapper;
import com.romanpulov.odeonwss.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrackService
        extends AbstractEntityService<Track, TrackDTO, TrackRepository>
        implements EditableObjectService<TrackDTO>{

    private final ArtifactTypeRepository artifactTypeRepository;
    private final ArtifactRepository artifactRepository;
    private final TrackTransformer transformer;
    private final MediaFileRepository mediaFileRepository;

    public TrackService(
            ArtifactRepository artifactRepository,
            ArtifactTypeRepository artifactTypeRepository,
            TrackRepository trackRepository,
            TrackMapper trackMapper,
            TrackTransformer transformer,
            MediaFileRepository mediaFileRepository,
            DVProductRepository dvProductRepository) {
        super(trackRepository, trackMapper);
        this.artifactRepository = artifactRepository;
        this.artifactTypeRepository = artifactTypeRepository;
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

    @Transactional
    public RowsAffectedDTO resetTrackNumbers(long artifactId) throws CommonEntityNotFoundException {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow
                (() -> new CommonEntityNotFoundException("Artifact", artifactId));
        long rowsAffected = 0;

        if (artifactTypeRepository.isVideo(artifact.getArtifactType().getId())) {
            List<Track> tracks = repository.findAllByArtifact(artifact)
                    .stream()
                    .filter(t -> t.getNum() != null)
                    .sorted(Comparator.comparingLong(Track::getNum))
                    .toList();

            long num = 1;
            for (Track track : tracks) {
                if (track.getNum() != null && track.getNum() != num) {
                    track.setNum(num);
                    rowsAffected ++;
                }
                num++;
            }
        }
        return RowsAffectedDTO.from(rowsAffected);
    }
}
