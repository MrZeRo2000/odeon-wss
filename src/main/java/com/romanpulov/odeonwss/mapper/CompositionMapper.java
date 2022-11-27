package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.entity.*;

import java.util.Set;
import java.util.stream.Collectors;

public class CompositionMapper {
    public static void updateFromEditDTO(Composition composition, CompositionEditDTO editDTO, Artifact artifact, Set<MediaFile> mediaFiles) {
        composition.setId(editDTO.getId());
        composition.setArtifact(artifact);

        if (editDTO.getArtistId() != null) {
            Artist artist = new Artist();
            artist.setId(editDTO.getArtistId());
            composition.setArtist(artist);
        } else {
            composition.setArtist(null);
        }

        if (editDTO.getPerformerArtistId() != null) {
            Artist performerArtist = new Artist();
            performerArtist.setId(editDTO.getPerformerArtistId());
            composition.setPerformerArtist(performerArtist);
        } else {
            composition.setPerformerArtist(null);
        }

        if (editDTO.getDvTypeId() != null) {
            DVType dvType = new DVType();
            dvType.setId(editDTO.getDvTypeId());
            composition.setDvType(dvType);
        } else {
            composition.setDvType(null);
        }

        composition.setTitle(editDTO.getTitle());
        composition.setDuration(editDTO.getDuration());
        composition.setDiskNum(editDTO.getDiskNum());
        composition.setNum(editDTO.getNum());

        composition.setMediaFiles(mediaFiles);
    }

    public static Composition createFromEditDTO(CompositionEditDTO editDTO, Artifact artifact) {
        Composition composition = new Composition();

        composition.setId(editDTO.getId());
        composition.setArtifact(artifact);

        if (editDTO.getArtistId() != null) {
            Artist artist = new Artist();
            artist.setId(editDTO.getArtistId());
            composition.setArtist(artist);
        }

        if (editDTO.getPerformerArtistId() != null) {
            Artist performerArtist = new Artist();
            performerArtist.setId(editDTO.getPerformerArtistId());
            composition.setPerformerArtist(performerArtist);
        }

        if (editDTO.getDvTypeId() != null) {
            DVType dvType = new DVType();
            dvType.setId(editDTO.getDvTypeId());
            composition.setDvType(dvType);
        }

        composition.setTitle(editDTO.getTitle());
        composition.setDuration(editDTO.getDuration());
        composition.setDiskNum(editDTO.getDiskNum());
        composition.setNum(editDTO.getNum());

        composition.setMediaFiles(editDTO.getMediaFileIds().stream().map(MediaFile::fromId).collect(Collectors.toSet()));

        return composition;
    }

    public static CompositionEditDTO toEditDTO(Composition composition) {
        CompositionEditDTO dto = new CompositionEditDTO();

        dto.setId(composition.getId());
        dto.setArtifactId(composition.getArtifact().getId());

        Artist artist = composition.getArtist();
        if (artist != null) {
            dto.setArtistId(artist.getId());
            dto.setArtistName(artist.getName());
        }

        Artist performerArtist = composition.getPerformerArtist();
        if (performerArtist != null) {
            dto.setPerformerArtistId(performerArtist.getId());
            dto.setPerformerArtistName(performerArtist.getName());
        }

        DVType dvType = composition.getDvType();
        if (dvType != null) {
            dto.setDvTypeId(dvType.getId());
            dto.setDvTypeName(dvType.getName());
        }

        dto.setTitle(composition.getTitle());
        dto.setDuration(composition.getDuration());
        dto.setDiskNum(composition.getDiskNum());
        dto.setNum(composition.getNum());
        dto.setMediaFiles(composition.getMediaFiles().stream().map(MediaFile::getId).collect(Collectors.toSet()));

        return dto;
    }
}
