package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtistCategoryArtistDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends PagingAndSortingRepository<Artist, Long> {
    List<Artist> getAllByType(ArtistType type);

    List<Artist> getAllByTypeOrderByName(ArtistType type);

    Optional<Artist> findFirstByTypeAndName(ArtistType type, String name);

    Optional<Artist> findFirstByName(String name);

    Optional<Artist> findFirstByMigrationId(Long migrationId);
}
