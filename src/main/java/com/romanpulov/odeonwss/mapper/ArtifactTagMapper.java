package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactTag;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Pair<Collection<ArtifactTag>, Collection<ArtifactTag>> mergeTags(
            List<ArtifactTag> oldTags, List<ArtifactTag> newTags) {
        Set<String> oldTagNames = oldTags.stream().map(ArtifactTag::getName).collect(Collectors.toSet());
        Set<String> newTagNames = newTags.stream().map(ArtifactTag::getName).collect(Collectors.toSet());

        List<ArtifactTag> inserted = newTags
                .stream()
                .filter(t -> !oldTagNames.contains(t.getName()))
                .toList();

        List<ArtifactTag> deleted = oldTags
                .stream()
                .filter(t -> !newTagNames.contains(t.getName()))
                .toList();

        return Pair.of(inserted, deleted);
    }
}
