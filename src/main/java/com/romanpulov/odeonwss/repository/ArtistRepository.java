package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artist;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends PagingAndSortingRepository<Artist, Long> {
    List<Artist> getAllByType(String type);

    Optional<Artist> findFirstByTypeAndName(String type, String name);
}
