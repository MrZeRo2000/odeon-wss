package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtistDTO;
import com.romanpulov.odeonwss.dto.ArtistFlatDTO;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface ArtistRepository extends EntityDTORepository<Artist, ArtistDTO> {
    List<Artist> getAllByType(ArtistType type);

    List<IdNameDTO> getByTypeOrderByName(ArtistType type);

    Optional<Artist> findFirstByTypeAndName(ArtistType type, String name);

    Optional<Artist> findFirstByName(String name);

    Optional<Artist> findFirstByMigrationId(Long migrationId);

    @Query(value = "SELECT " +
            "ar.id AS id, " +
            "ar.name AS artistName, " +
            "ar.type AS artistType, " +
            "ac.type AS categoryType, " +
            "ac.name AS categoryName, " +
            "ad.id AS detailId, " +
            "ad.biography AS artistBiography " +
            "FROM Artist AS ar " +
            "LEFT OUTER JOIN ArtistCategory AS ac ON ar.id = ac.artist.id " +
            "LEFT OUTER JOIN ArtistDetail AS ad ON ar.id = ad.artist.id " +
            "WHERE ar.id = :id " +
            "ORDER BY ac.type, ac.name"
    )
    List<ArtistFlatDTO> findFlatDTOById(long id);

    @Query(value = "SELECT " +
            "ar.arts_id AS id, " +
            "ar.arts_name AS artistName, " +
            "ar.arts_type_code AS artistTypeCode, " +
            "ac.atct_type_code AS categoryTypeCode, " +
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
