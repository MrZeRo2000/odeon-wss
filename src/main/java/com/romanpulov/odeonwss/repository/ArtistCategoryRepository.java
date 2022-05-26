package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ArtistCategoryRepository extends PagingAndSortingRepository<ArtistCategory, Long> {

    List<ArtistCategory> getArtistCategoriesByArtistOrderByName(Artist artist);

    @Transactional
    void deleteAllByArtist(Artist artist);
}
