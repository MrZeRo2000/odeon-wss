package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactTag;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArtifactTagMapper {
    public List<ArtifactTag> createFromArtifactDTO(Artifact entity, ArtifactDTO dto) {
        return dto.getTags()
                .stream()
                .distinct()
                .sorted()
                .map(v -> {
                    ArtifactTag tag = new ArtifactTag();
                    tag.setArtifact(entity);
                    tag.setName(v);
                    return tag;
                })
                .toList();
    }
}
