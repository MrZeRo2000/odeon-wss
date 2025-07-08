package com.romanpulov.odeonwss.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.jutilscore.io.FileUtils;
import com.romanpulov.odeonwss.dto.*;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataGenerator {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private TrackRepository trackRepository;

    public void generateFromJSON(String json) throws JsonProcessingException {
        DataGeneratorDTO data = mapper.readValue(json, DataGeneratorDTO.class);

        Map<String, Artist> artistMap = data.getArtists() == null ?
                null :
                createArtists(data.getArtists());

        Map<String, Artifact> artifactMap = data.getArtifacts() == null ?
                null :
                createArtifacts(data.getArtifacts(), artistMap);

        Map<String, MediaFile> mediaFileMap = data.getMediaFiles() == null ?
                null :
                createMediaFiles(data.getMediaFiles(), artifactMap);

        if ((data.getTracks() != null) && !data.getTracks().isEmpty()) {
            createTracks(data.getTracks(), artifactMap, mediaFileMap);
        }
    }

    public void createArtistsFromList(Collection<String> artists) {
        createArtists(artists.stream().map( s -> {
            ArtistDTOImpl a = new ArtistDTOImpl();
            a.setArtistType(ArtistType.ARTIST);
            a.setArtistName(s);
            return a;
        }).toList());
    }

    private Map<String, Artist> createArtists(Collection<? extends ArtistDTO> artists) {
        Map<String, Artist> result = new HashMap<>();
        for (ArtistDTO artist : artists) {
            if ((artist.getArtistName() != null) && !artist.getArtistName().isEmpty()) {
                Artist newArtist = new Artist();
                newArtist.setName(artist.getArtistName());
                newArtist.setType(artist.getArtistType());

                artistRepository.save(newArtist);

                result.put(artist.getArtistName(), newArtist);
            }
        }

        return result;
    }

    private Map<String, MediaFile> createMediaFiles(Collection<MediaFileDTO> mediaFiles, Map<String, Artifact> artifactMap) {
        Map<String, MediaFile> result = new HashMap<>();
        for (MediaFileDTO mediaFile : mediaFiles) {
            if ((mediaFile.getName() != null) && !mediaFile.getName().isEmpty()) {
                MediaFile newMediaFile = new MediaFile();
                newMediaFile.setName(mediaFile.getName());
                newMediaFile.setDuration(mediaFile.getDuration());
                newMediaFile.setSize(Optional.ofNullable(mediaFile.getSize()).orElse(0L));
                newMediaFile.setBitrate(mediaFile.getBitrate());
                newMediaFile.setFormat(FileUtils.getExtension(mediaFile.getName()).toUpperCase());

                if (mediaFile.getArtifactTitle() != null) {
                    newMediaFile.setArtifact(artifactMap.get(mediaFile.getArtifactTitle()));
                }

                mediaFileRepository.save(newMediaFile);

                result.put(mediaFile.getName(), newMediaFile);
            }
        }

        return result;
    }

    private Map<String, Artifact> createArtifacts(Collection<ArtifactDTO> artifacts, Map<String, Artist> artistMap) {
        Map<String, Artifact> result = new HashMap<>();

        for (ArtifactDTO artifact : artifacts) {
            if ((artifact.getArtifactType() != null) && (artifact.getTitle() != null) && !artifact.getTitle().isEmpty()) {
                Artifact newArtifact = new Artifact();
                newArtifact.setArtifactType(artifactTypeRepository.findById(artifact.getArtifactType().getId()).orElseThrow());
                newArtifact.setTitle(artifact.getTitle());
                newArtifact.setDuration(artifact.getDuration());
                newArtifact.setSize(artifact.getSize());

                if (artifact.getArtist() != null) {
                    newArtifact.setArtist(artistMap.get(artifact.getArtist().getArtistName()));
                }

                artifactRepository.save(newArtifact);
                result.put(artifact.getTitle(), newArtifact);
            }
        }

        return result;
    }

    private void createTracks(
            Collection<TrackDTO> tracks,
            Map<String, Artifact> artifactMap,
            Map<String, MediaFile> mediaFiles) {
        for (TrackDTO track : tracks) {
            if (track.getArtifact() != null && (track.getTitle() != null) && !track.getTitle().isEmpty()) {
                Track newTrack = new Track();
                newTrack.setArtifact(artifactMap.get(track.getArtifact().getTitle()));
                newTrack.setTitle(track.getTitle());
                newTrack.setDuration(track.getDuration());

                if ((track.getMediaFiles() != null) && !track.getMediaFiles().isEmpty()) {
                    Set<MediaFile> mediaFileSet = track
                            .getMediaFiles()
                            .stream()
                            .map(v -> mediaFiles.get(v.getName()))
                            .collect(Collectors.toSet());
                    newTrack.setMediaFiles(mediaFileSet);
                }

                trackRepository.save(newTrack);
            }
        }
    }
}
