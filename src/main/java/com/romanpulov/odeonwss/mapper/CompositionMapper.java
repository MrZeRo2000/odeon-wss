package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.MediaFile;

import java.util.Set;
import java.util.stream.Collectors;

public class CompositionMapper {
    public static void updateFromEditDTO(Composition composition, CompositionEditDTO editDTO, Artifact artifact, Set<MediaFile> mediaFiles) {
        composition.setId(editDTO.getId());
        composition.setArtifact(artifact);
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
        dto.setTitle(composition.getTitle());
        dto.setDuration(composition.getDuration());
        dto.setDiskNum(composition.getDiskNum());
        dto.setNum(composition.getNum());
        dto.setMediaFiles(composition.getMediaFiles().stream().map(MediaFile::getId).collect(Collectors.toSet()));

        return dto;
    }
}
