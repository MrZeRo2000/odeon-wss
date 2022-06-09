package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtistCategoryArtistDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryDetailDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ArtistCategoryRepository extends PagingAndSortingRepository<ArtistCategory, Long> {

    List<ArtistCategory> getArtistCategoriesByArtistOrderByTypeAscNameAsc(Artist artist);

    List<ArtistCategory> findByOrderByArtistNameAsc();

    @Query("SELECT " +
            "new com.romanpulov.odeonwss.dto.ArtistCategoryArtistDTO(" +
            "ar.id, " +
            "ar.name, " +
            "ar.type, " +
            "ac.type, " +
            "ac.name, " +
            "ad.id) " +
            "FROM Artist AS ar " +
            "LEFT OUTER JOIN ArtistCategory AS ac ON ac.artist = ar " +
            "LEFT OUTER JOIN ArtistDetail AS ad ON ad.artist = ar " +
            "ORDER BY ar.name, ac.type, ac.name")
    List<ArtistCategoryArtistDTO> getAllWithArtistOrdered();

    @Query("SELECT " +
            "new com.romanpulov.odeonwss.dto.ArtistCategoryArtistDTO(" +
            "ar.id, " +
            "ar.name, " +
            "ar.type, " +
            "ac.type, " +
            "ac.name, " +
            "ad.id) " +
            "FROM Artist AS ar " +
            "LEFT OUTER JOIN ArtistCategory AS ac ON ac.artist = ar " +
            "LEFT OUTER JOIN ArtistDetail AS ad ON ad.artist = ar " +
            "WHERE ar.id = :id " +
            "ORDER BY ar.name, ac.type, ac.name")
    List<ArtistCategoryArtistDTO> getAllWithArtistByIdOrdered(Long id);

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

    @Transactional
    void deleteAllByArtist(Artist artist);

    Optional<ArtistCategory> findFirstByMigrationId(Long migrationId);
}
