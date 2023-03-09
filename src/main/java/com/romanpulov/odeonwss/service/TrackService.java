package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.TrackEditDTO;
import com.romanpulov.odeonwss.dto.TrackTableDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.TrackMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TrackService implements EditableObjectService<TrackEditDTO>{

    private final ArtifactRepository artifactRepository;

    private final TrackRepository trackRepository;

    private final MediaFileRepository mediaFileRepository;

    public TrackService(ArtifactRepository artifactRepository, TrackRepository trackRepository, MediaFileRepository mediaFileRepository) {
        this.artifactRepository = artifactRepository;
        this.trackRepository = trackRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    public List<TrackTableDTO>getTable(Long artifactId) throws CommonEntityNotFoundException {
        Optional<Artifact> existingArtifact = artifactRepository.findById(artifactId);
        if (existingArtifact.isPresent()) {
            return trackRepository.getTrackTableByArtifact(existingArtifact.get());
        } else {
            throw new CommonEntityNotFoundException("Artifact", artifactId);
        }
    }

    @Override
    @Transactional
    public TrackEditDTO getById(Long id) throws CommonEntityNotFoundException {
        Optional<Track> existingTrack = trackRepository.findById(id);
        if (existingTrack.isPresent()) {
            return TrackMapper.toEditDTO(existingTrack.get());
        } else {
            throw new CommonEntityNotFoundException("Track", id);
        }
    }

    @Override
    @Transactional
    public TrackEditDTO insert(TrackEditDTO o) throws CommonEntityNotFoundException {
        Artifact artifact = artifactRepository.findById(o.getArtifactId())
                .orElseThrow(() -> new CommonEntityNotFoundException("Artifact", o.getArtifactId()));
        Track track = TrackMapper.createFromEditDTO(o, artifact);

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
        Set<MediaFile> mediaFiles = StreamSupport.stream(mediaFileRepository.findAllById(o.getMediaFileIds()).spliterator(), false).collect(Collectors.toSet());

        TrackMapper.updateFromEditDTO(track, o, artifact, mediaFiles);

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