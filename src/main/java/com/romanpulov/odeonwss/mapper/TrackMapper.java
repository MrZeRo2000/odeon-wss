package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.TrackEditDTO;
import com.romanpulov.odeonwss.entity.*;

import java.util.Set;
import java.util.stream.Collectors;

public class TrackMapper {
    public static void updateFromEditDTO(Track track, TrackEditDTO editDTO, Artifact artifact, Set<MediaFile> mediaFiles) {
        track.setId(editDTO.getId());
        track.setArtifact(artifact);

        if (editDTO.getArtistId() != null) {
            Artist artist = new Artist();
            artist.setId(editDTO.getArtistId());
            track.setArtist(artist);
        } else {
            track.setArtist(null);
        }

        if (editDTO.getPerformerArtistId() != null) {
            Artist performerArtist = new Artist();
            performerArtist.setId(editDTO.getPerformerArtistId());
            track.setPerformerArtist(performerArtist);
        } else {
            track.setPerformerArtist(null);
        }

        if (editDTO.getDvTypeId() != null) {
            DVType dvType = new DVType();
            dvType.setId(editDTO.getDvTypeId());
            track.setDvType(dvType);
        } else {
            track.setDvType(null);
        }

        track.setTitle(editDTO.getTitle());
        track.setDuration(editDTO.getDuration());
        track.setDiskNum(editDTO.getDiskNum());
        track.setNum(editDTO.getNum());

        track.setMediaFiles(mediaFiles);
    }

    public static Track createFromEditDTO(TrackEditDTO editDTO, Artifact artifact) {
        Track track = new Track();

        track.setId(editDTO.getId());
        track.setArtifact(artifact);

        if (editDTO.getArtistId() != null) {
            Artist artist = new Artist();
            artist.setId(editDTO.getArtistId());
            track.setArtist(artist);
        }

        if (editDTO.getPerformerArtistId() != null) {
            Artist performerArtist = new Artist();
            performerArtist.setId(editDTO.getPerformerArtistId());
            track.setPerformerArtist(performerArtist);
        }

        if (editDTO.getDvTypeId() != null) {
            DVType dvType = new DVType();
            dvType.setId(editDTO.getDvTypeId());
            track.setDvType(dvType);
        }

        track.setTitle(editDTO.getTitle());
        track.setDuration(editDTO.getDuration());
        track.setDiskNum(editDTO.getDiskNum());
        track.setNum(editDTO.getNum());

        track.setMediaFiles(editDTO.getMediaFileIds().stream().map(MediaFile::fromId).collect(Collectors.toSet()));

        return track;
    }

    public static TrackEditDTO toEditDTO(Track track) {
        TrackEditDTO dto = new TrackEditDTO();

        dto.setId(track.getId());
        dto.setArtifactId(track.getArtifact().getId());

        Artist artist = track.getArtist();
        if (artist != null) {
            dto.setArtistId(artist.getId());
            dto.setArtistName(artist.getName());
        }

        Artist performerArtist = track.getPerformerArtist();
        if (performerArtist != null) {
            dto.setPerformerArtistId(performerArtist.getId());
            dto.setPerformerArtistName(performerArtist.getName());
        }

        DVType dvType = track.getDvType();
        if (dvType != null) {
            dto.setDvTypeId(dvType.getId());
            dto.setDvTypeName(dvType.getName());
        }

        dto.setTitle(track.getTitle());
        dto.setDuration(track.getDuration());
        dto.setDiskNum(track.getDiskNum());
        dto.setNum(track.getNum());
        dto.setMediaFiles(track.getMediaFiles().stream().map(MediaFile::getId).collect(Collectors.toSet()));

        return dto;
    }
}
