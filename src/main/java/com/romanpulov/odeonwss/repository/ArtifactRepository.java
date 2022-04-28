package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Artist;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ArtifactRepository extends PagingAndSortingRepository<Artifact, Long> {
    List<Artifact> getArtifactsByArtist(Artist artist);
}
