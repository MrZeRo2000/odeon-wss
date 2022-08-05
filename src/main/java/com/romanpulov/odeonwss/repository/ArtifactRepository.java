package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtifactEditDTO;
import com.romanpulov.odeonwss.dto.ArtifactTableDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

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

    @Query(
        "SELECT new com.romanpulov.odeonwss.dto.ArtifactTableDTO(" +
                "a.id," +
                "at.name," +
                "ar.name, " +
                "par.name, " +
                "a.title, " +
                "a.year," +
                "a.duration," +
                "a.size," +
                "a.insertDate" +
                ") " +
                "FROM Artifact as a " +
                "INNER JOIN ArtifactType as at ON a.artifactType = at " +
                "INNER JOIN Artist as ar ON a.artist = ar " +
                "LEFT OUTER JOIN Artist as par ON a.performerArtist = par " +
                "WHERE ar.type=:artistType " +
                "AND a.artifactType IN (:artifactTypes) " +
                "ORDER BY ar.name, a.year, a.title"
    )
    List<ArtifactTableDTO> getArtifactTableByArtistTypeAndArtifactTypes(@Param("artistType") ArtistType artistType, @Param("artifactTypes") List<ArtifactType> artifactTypes);

    @Query(
            "SELECT new com.romanpulov.odeonwss.dto.ArtifactEditDTO(" +
                    "a.id, " +
                    "a.artifactType.id, " +
                    "a.artist.id, " +
                    "ar.name, " +
                    "a.performerArtist.id, " +
                    "par.name, " +
                    "a.title, " +
                    "a.year, " +
                    "a.duration, " +
                    "a.size" +
                    ") " +
                    "FROM Artifact as a " +
                    "INNER JOIN Artist as ar ON a.artist = ar " +
                    "LEFT OUTER JOIN Artist as par ON a.performerArtist = par " +
                    "WHERE a.id = :id"
    )
    Optional<ArtifactEditDTO> getArtifactEditById(Long id);
}
