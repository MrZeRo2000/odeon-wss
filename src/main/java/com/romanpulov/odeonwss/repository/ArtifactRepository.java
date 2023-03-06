package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtifactTableDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.view.IdTitleView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ArtifactRepository extends MappedMigratedIdJpaRepository<Artifact, Long> {
    List<Artifact> getAllByArtifactType(ArtifactType artifactType);

    @Query("SELECT a FROM Artifact a LEFT JOIN FETCH a.tracks WHERE a.artifactType = :artifactType")
    List<Artifact> getAllByArtifactTypeWithTracks(ArtifactType artifactType);

    @Query("SELECT a FROM Artifact a LEFT JOIN FETCH a.tracks WHERE a.id = :id")
    Optional<Artifact> getByIdsWithTracks(Long id);

    default Map<Long, Artifact> findAllByArtifactTypeMigrationIdMap(ArtifactType artifactType) {
        return getAllByArtifactType(artifactType)
                .stream()
                .filter(v -> v.getMigrationId() != null)
                .collect(Collectors.toMap(Artifact::getMigrationId, v -> v));
    }

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
                "ar.type, " +
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
                "LEFT OUTER JOIN Artist as ar ON a.artist = ar " +
                "LEFT OUTER JOIN Artist as par ON a.performerArtist = par " +
                "WHERE (at.parentId = 200 OR ar.type=:artistType) " +
                "AND a.artifactType IN (:artifactTypes) " +
                "ORDER BY ar.name, a.year, a.title"
    )
    List<ArtifactTableDTO> getArtifactTableByArtistTypeAndArtifactTypes(@Param("artistType") ArtistType artistType, @Param("artifactTypes") List<ArtifactType> artifactTypes);

    @Query(
            "SELECT a " +
            "FROM Artifact as a " +
            "WHERE a.artist.type = :artistType"
    )
    List<IdTitleView> getArtifactsByArtistType(@Param("artistType") ArtistType artistType);

    @Query(
            "SELECT a " +
                    "FROM Artifact as a " +
                    "INNER JOIN FETCH a.artifactType " +
                    "LEFT JOIN FETCH a.artist " +
                    "LEFT JOIN FETCH a.performerArtist " +
                    "WHERE a.id = :id"
    )
    Optional<Artifact> findArtifactEditById(Long id);

    @Query("SELECT a FROM Artifact a INNER JOIN FETCH a.artist LEFT JOIN FETCH a.performerArtist WHERE a.title = :title")
    Optional<Artifact> getArtifactWithArtistByTitle(String title);
}
