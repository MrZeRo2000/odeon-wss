package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface ArtistCategoryRepository extends CrudRepository<ArtistCategory, Long> {

    List<ArtistCategory> getArtistCategoriesByArtistOrderByTypeAscNameAsc(Artist artist);

    List<ArtistCategory> findByOrderByArtistNameAsc();

    Optional<ArtistCategory> findFirstByMigrationId(Long migrationId);
}
