package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtifactEditDTO;
import com.romanpulov.odeonwss.dto.ArtifactTableDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.view.IdNameView;
import com.romanpulov.odeonwss.view.IdTitleView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArtifactRepository extends JpaRepository<Artifact, Long> {
    List<Artifact> getAllByArtifactType(ArtifactType artifactType);

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
                "INNER JOIN Artist as ar ON a.artist = ar " +
                "LEFT OUTER JOIN Artist as par ON a.performerArtist = par " +
                "WHERE ar.type=:artistType " +
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
            "SELECT new com.romanpulov.odeonwss.dto.ArtifactEditDTO(" +
                    "a.id, " +
                    "a.artifactType.id, " +
                    "a.artist.type, " +
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

    @Query("SELECT a FROM Artifact a INNER JOIN FETCH a.artist LEFT JOIN FETCH a.performerArtist WHERE a.title = :title")
    Optional<Artifact> getArtifactWithArtistByTitle(String title);
}
