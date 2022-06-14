package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtifactEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;

import java.time.LocalDate;

public class ArtifactMapper {
    public static Artifact createFromArtifactEditDTO(ArtifactEditDTO aed) {
        Artifact artifact = new Artifact();
        artifact.setId(aed.getId());

        ArtifactType artifactType = new ArtifactType();
        artifactType.setId(aed.getArtifactTypeId());
        artifact.setArtifactType(artifactType);

        Artist artist = new Artist();
        artist.setId(aed.getArtistId());
        artifact.setArtist(artist);

        artifact.setTitle(aed.getTitle());
        artifact.setYear(aed.getYear());
        artifact.setDuration(aed.getDuration());
        artifact.setSize(aed.getSize());

        if (aed.getId() == null) {
            artifact.setInsertDate(LocalDate.now());
        }

        return artifact;
    }
}
