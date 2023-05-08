package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtifactEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import org.springframework.stereotype.Component;

@Component
public class ArtifactMapper {
    public ArtifactEditDTO toDTO(Artifact a) {
        return new ArtifactEditDTO(
                a.getId(),
                a.getArtifactType().getId(),
                a.getArtist(),
                a.getPerformerArtist(),
                a.getTitle(),
                a.getYear(),
                a.getDuration(),
                a.getSize()
        );
    }

    public Artifact fromDTO(ArtifactEditDTO aed) {
        Artifact artifact = new Artifact();
        artifact.setId(aed.getId());

        ArtifactType artifactType = new ArtifactType();
        artifactType.setId(aed.getArtifactTypeId());
        artifact.setArtifactType(artifactType);

        if (aed.getArtistId() != null) {
            Artist artist = new Artist();
            artist.setId(aed.getArtistId());
            artifact.setArtist(artist);
        }

        if (aed.getPerformerArtistId() != null) {
            Artist performerArtist = new Artist();
            performerArtist.setId(aed.getPerformerArtistId());
            artifact.setPerformerArtist(performerArtist);
        }

        artifact.setTitle(aed.getTitle());
        artifact.setYear(aed.getYear());
        artifact.setDuration(aed.getDuration());
        artifact.setSize(aed.getSize());

        return artifact;
    }
}
