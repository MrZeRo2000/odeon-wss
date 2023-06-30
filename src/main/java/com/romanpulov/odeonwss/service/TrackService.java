package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.TrackDTO;
import com.romanpulov.odeonwss.dto.TrackEditDTO;
import com.romanpulov.odeonwss.dto.TrackTransformer;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.DVProduct;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.TrackMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class TrackService implements EditableObjectService<TrackEditDTO>{

    private final ArtifactRepository artifactRepository;

    private final TrackRepository trackRepository;

    private final TrackMapper trackMapper;

    private final TrackTransformer transformer;

    private final MediaFileRepository mediaFileRepository;

    private final DVProductRepository dvProductRepository;

    public TrackService(
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            TrackMapper trackMapper,
            TrackTransformer transformer,
            MediaFileRepository mediaFileRepository,
            DVProductRepository dvProductRepository) {
        this.artifactRepository = artifactRepository;
        this.trackRepository = trackRepository;
        this.trackMapper = trackMapper;
        this.transformer = transformer;
        this.mediaFileRepository = mediaFileRepository;
        this.dvProductRepository = dvProductRepository;
    }

    public List<TrackDTO>getTable(Long artifactId) throws CommonEntityNotFoundException {
        Optional<Artifact> existingArtifact = artifactRepository.findById(artifactId);
        if (existingArtifact.isPresent()) {
            return transformer.transform(trackRepository.findAllFlatDTOByArtifact(existingArtifact.get()));
        } else {
            throw new CommonEntityNotFoundException("Artifact", artifactId);
        }
    }

    public List<TrackDTO> getTableByArtifactTypeId(Long artifactTypeId) {
        return transformer.transform(trackRepository.findAllFlatDTOByArtifactTypeId(artifactTypeId));
    }

    public List<TrackDTO> getTableByProductId(Long productId) {
        return transformer.transform(trackRepository.findAllFlatDTOByDvProductId(productId));
    }

    @Override
    @Transactional
    public TrackEditDTO getById(Long id) throws CommonEntityNotFoundException {
        Optional<Track> existingTrack = trackRepository.findById(id);
        if (existingTrack.isPresent()) {
            return trackMapper.toDTO(existingTrack.get());
        } else {
            throw new CommonEntityNotFoundException("Track", id);
        }
    }

    @Override
    @Transactional
    public TrackEditDTO insert(TrackEditDTO o) throws CommonEntityNotFoundException {
        Artifact artifact = artifactRepository.findById(o.getArtifactId())
                .orElseThrow(() -> new CommonEntityNotFoundException("Artifact", o.getArtifactId()));
        Track track = trackMapper.fromDTO(o, artifact);

        trackRepository.save(track);

        return getById(track.getId());
    }

    @Override
    @Transactional
    public TrackEditDTO update(TrackEditDTO o) throws CommonEntityNotFoundException {
        Artifact artifact = artifactRepository.findById(o.getArtifactId())
                .orElseThrow(() -> new CommonEntityNotFoundException("Artifact", o.getArtifactId()));
        Track track = trackRepository.findById(o.getId())
                .orElseThrow(() -> new CommonEntityNotFoundException("Track", o.getId()));
        Set<MediaFile> mediaFiles = StreamSupport
                .stream(mediaFileRepository.findAllById(o.getMediaFileIds()).spliterator(), false)
                .collect(Collectors.toSet());
        Set<DVProduct> dvProducts = o.getDvProductId() == null ? new HashSet<>() :
                Stream
                .ofNullable(dvProductRepository.findById(o.getDvProductId()).orElse(null))
                .collect(Collectors.toSet());

        trackMapper.update(track, o, artifact, mediaFiles, dvProducts);

        trackRepository.save(track);

        return getById(o.getId());
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws CommonEntityNotFoundException {
        Optional<Track> existingTrack = trackRepository.findById(id);
        if (existingTrack.isPresent()) {
            trackRepository.delete(existingTrack.get());
        } else {
            throw new CommonEntityNotFoundException("Track", id);
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
        trackRepository.save(track);
    }

    @Transactional
    public void insertTracksWithMedia(Iterable<Track> tracks, Iterable<MediaFile> mediaFiles) {
        mediaFileRepository.saveAll(mediaFiles);
        trackRepository.saveAll(tracks);
    }
}
