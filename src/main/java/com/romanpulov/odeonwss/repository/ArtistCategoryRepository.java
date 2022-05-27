package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtistCategoryArtistDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ArtistCategoryRepository extends PagingAndSortingRepository<ArtistCategory, Long> {

    List<ArtistCategory> getArtistCategoriesByArtistOrderByName(Artist artist);

    List<ArtistCategory> findByOrderByArtistNameAsc();

    @Query("SELECT " +
            "new com.romanpulov.odeonwss.dto.ArtistCategoryArtistDTO(" +
            "ar.id, " +
            "ar.name, " +
            "ac.type, " +
            "ac.name) " +
            "FROM Artist AS ar " +
            "LEFT OUTER JOIN ArtistCategory AS ac ON ac.artist = ar " +
            "ORDER BY ar.name, ac.type, ac.name")
    List<ArtistCategoryArtistDTO> getAllWithArtistOrdered();

    @Query("SELECT " +
            "new com.romanpulov.odeonwss.dto.ArtistCategoryArtistDTO(" +
            "ar.id, " +
            "ar.name, " +
            "ac.type, " +
            "ac.name) " +
            "FROM Artist AS ar " +
            "LEFT OUTER JOIN ArtistCategory AS ac ON ac.artist = ar " +
            "WHERE ar.id = :id " +
            "ORDER BY ar.name, ac.type, ac.name")
    List<ArtistCategoryArtistDTO> getAllWithArtistByIdOrdered(Long id);

    @Transactional
    void deleteAllByArtist(Artist artist);
}
