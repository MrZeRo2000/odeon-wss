package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtistFlatDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface ArtistRepository extends PagingAndSortingRepository<Artist, Long> {
    List<Artist> getAllByType(ArtistType type);

    List<Artist> getAllByTypeOrderByName(ArtistType type);

    List<IdNameDTO> getByTypeOrderByName(ArtistType type);

    Optional<Artist> findFirstByTypeAndName(ArtistType type, String name);

    Optional<Artist> findFirstByName(String name);

    Optional<Artist> findFirstByMigrationId(Long migrationId);

    @Query("SELECT " +
            "ar.id AS id, " +
            "ar.name AS artistName, " +
            "ar.type AS artistType, " +
            "ac.type AS categoryType, " +
            "ac.name AS categoryName, " +
            "ad.id AS detailId " +
            "FROM Artist AS ar " +
            "LEFT OUTER JOIN ArtistCategory AS ac ON ac.artist = ar " +
            "LEFT OUTER JOIN ArtistDetail AS ad ON ad.artist = ar " +
            "ORDER BY ar.name, ac.type, ac.name")
    List<ArtistFlatDTO> findAllFlatDTO();
}
