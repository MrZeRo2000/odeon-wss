package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtistCategoryDetailDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface ArtistCategoryRepository extends CrudRepository<ArtistCategory, Long> {

    List<ArtistCategory> getArtistCategoriesByArtistOrderByTypeAscNameAsc(Artist artist);

    List<ArtistCategory> findByOrderByArtistNameAsc();

    @Query("SELECT " +
            "new com.romanpulov.odeonwss.dto.ArtistCategoryDetailDTO(" +
            "ar.id, " +
            "ar.type, " +
            "ar.name, " +
            "ad.biography, " +
            "ac.type, " +
            "ac.name) " +
            "FROM Artist AS ar " +
            "LEFT OUTER JOIN ArtistCategory AS ac ON ac.artist = ar " +
            "LEFT OUTER JOIN ArtistDetail AS ad ON ad.artist = ar " +
            "WHERE ar.id = :id " +
            "ORDER BY ac.type, ac.name")
    List<ArtistCategoryDetailDTO> getArtistCategoryDetailsByArtistId(Long id);

    Optional<ArtistCategory> findFirstByMigrationId(Long migrationId);
}
