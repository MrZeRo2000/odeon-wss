package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ArtifactRepository extends PagingAndSortingRepository<Artifact, Long> {
    List<Artifact> getArtifactsByArtist(Artist artist);

    Optional<Artifact> findFirstByArtifactTypeAndArtistAndTitleAndYear(
            ArtifactType artifactType,
            Artist artist,
            String title,
            Long year
    );
}
