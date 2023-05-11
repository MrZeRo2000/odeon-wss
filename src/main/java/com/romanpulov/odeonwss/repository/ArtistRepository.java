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

    @Query(value = "SELECT " +
            "ar.arts_id AS id, " +
            "ar.arts_name AS artistName, " +
            "ar.arts_type_code AS artistType, " +
            "ac.atct_type_code AS categoryType, " +
            "ac.atct_name AS categoryName, " +
            "ad.atdt_id AS detailId, " +
            "CASE WHEN EXISTS(SELECT 1 FROM artist_lyrics al WHERE ar.arts_id = al.arts_id) THEN 1 END AS hasLyrics " +
            "FROM main.artists AS ar " +
            "LEFT OUTER JOIN main.artist_categories AS ac ON ac.arts_id = ar.arts_id " +
            "LEFT OUTER JOIN main.artist_details AS ad ON ad.arts_id = ar.arts_id " +
            "ORDER BY ar.arts_name, ac.atct_type_code, ac.atct_name",
            nativeQuery = true)
    List<ArtistFlatDTO> findAllFlatDTO();
}
