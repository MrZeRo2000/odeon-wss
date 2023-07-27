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
        if ((dto.getArtist() != null) && (dto.getArtist().getId() != null)) {
            Artist artist = new Artist();
            artist.setId(dto.getArtist().getId());
            entity.setArtist(artist);
        } else {
            entity.setArtist(null);
        }

        if ((dto.getPerformerArtist() != null) && (dto.getPerformerArtist().getId() != null)) {
            Artist performerArtist = new Artist();
            performerArtist.setId(dto.getPerformerArtist().getId());
            entity.setPerformerArtist(performerArtist);
        } else {
            entity.setPerformerArtist(null);
        }

        entity.setTitle(dto.getTitle());
        entity.setYear(dto.getYear());
        entity.setDuration(dto.getDuration());
        entity.setSize(dto.getSize());
    }
}
