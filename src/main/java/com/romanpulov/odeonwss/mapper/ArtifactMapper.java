package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import org.springframework.stereotype.Component;

@Component
public class ArtifactMapper implements EntityDTOMapper<Artifact, ArtifactDTO> {
    @Override
    public String getEntityName() {
        return "Artifact";
    }

    @Override
    public Artifact fromDTO(ArtifactDTO dto) {
        Artifact artifact = new Artifact();

        // immutable fields
        artifact.setId(dto.getId());
        ArtifactType artifactType = new ArtifactType();
        artifactType.setId(dto.getArtifactType().getId());
        artifact.setArtifactType(artifactType);

        // mutable fields
        this.update(artifact, dto);

        return artifact;
    }

    @Override
    public void update(Artifact entity, ArtifactDTO dto) {
        entity.setArtist(MapperUtils.createEntityFromDTO(dto.getArtist(), Artist.class));
        entity.setPerformerArtist(MapperUtils.createEntityFromDTO(dto.getPerformerArtist(), Artist.class));

        entity.setTitle(dto.getTitle());
        entity.setYear(dto.getYear());
        entity.setDuration(dto.getDuration());
        entity.setSize(dto.getSize());
    }
}
