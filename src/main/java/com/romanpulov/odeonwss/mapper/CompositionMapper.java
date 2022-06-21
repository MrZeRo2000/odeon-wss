package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Composition;

public class CompositionMapper {
    public static Composition fromEditDTO(CompositionEditDTO editDTO, Artifact artifact) {
        Composition composition = new Composition();

        composition.setId(editDTO.getId());
        composition.setArtifact(artifact);
        composition.setTitle(editDTO.getTitle());
        composition.setDuration(editDTO.getDuration());
        composition.setDiskNum(editDTO.getDiskNum());
        composition.setNum(editDTO.getNum());

        return composition;
    }
}
