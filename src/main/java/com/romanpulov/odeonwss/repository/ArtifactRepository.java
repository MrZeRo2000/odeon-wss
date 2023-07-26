package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtifactFlatDTO;
import com.romanpulov.odeonwss.dto.ArtifactTableDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.dto.IdTitleDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public interface ArtifactRepository extends MappedMigratedIdJpaRepository<Artifact, Long> {
    List<Artifact> getAllByArtifactType(ArtifactType artifactType);

    @Query("SELECT a FROM Artifact a LEFT JOIN FETCH a.tracks WHERE a.artifactType = :artifactType")
    List<Artifact> getAllByArtifactTypeWithTracks(ArtifactType artifactType);

    @Query("SELECT a FROM Artifact a LEFT JOIN FETCH a.tracks LEFT JOIN FETCH a.artist WHERE a.artifactType = :artifactType AND a.tracks IS EMPTY")
    List<Artifact> getAllByArtifactTypeWithoutTracks(ArtifactType artifactType);

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

    Optional<Artifact> findFirstByArtifactTypeAndArtistNameAndTitleAndYear(
            ArtifactType artifactType,
            String artistName,
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
                "a.insertDateTime" +
                ") " +
                "FROM Artifact as a " +
                "INNER JOIN ArtifactType as at ON a.artifactType = at " +
                "LEFT OUTER JOIN Artist as ar ON a.artist = ar " +
                "LEFT OUTER JOIN Artist as par ON a.performerArtist = par " +
                "WHERE (at.parentId = 200 OR ar.type IS NULL OR ar.type=:artistType) " +
                "AND a.artifactType.id IN (:artifactTypeIds) " +
                "ORDER BY ar.name, a.year, a.title"
    )
    List<ArtifactTableDTO> getArtifactTableByArtistTypeAndArtifactTypeIds(ArtistType artistType, List<Long> artifactTypeIds);

    @Query(
            "SELECT a " +
            "FROM Artifact as a " +
            "WHERE a.artist.type = :artistType"
    )
    List<IdTitleDTO> getArtifactsByArtistType(@Param("artistType") ArtistType artistType);

    @Query(value ="""
        SELECT DISTINCT
          artf_id AS id,
          arts_name AS artistName,
          artf_title AS title,
          artf_year AS year
        FROM (
        SELECT
            ar.arts_name,
            t.artf_id,
            a.artf_title,
            a.artf_year,
            t.trck_num,
            ROW_NUMBER() OVER (PARTITION BY t.artf_id, t.trck_disk_num ORDER BY t.trck_num) AS trck_num_row
        FROM tracks t
        INNER JOIN artifacts a on t.artf_id = a.artf_id
        LEFT OUTER JOIN artists ar ON a.arts_id = ar.arts_id
        WHERE a.attp_id IN (:artifactTypeIds)
          AND (ar.arts_type_code IS NULL OR ar.arts_type_code IN (:artistTypeCodes))
        )
        WHERE trck_num != trck_num_row
    """,nativeQuery = true)
    List<ArtifactFlatDTO> getArtifactsWithNoMonotonicallyIncreasingTrackNumbers(List<String> artistTypeCodes, List<Long> artifactTypeIds);

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
