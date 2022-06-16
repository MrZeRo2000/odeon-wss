package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtistIdNameDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends PagingAndSortingRepository<Artist, Long> {
    List<Artist> getAllByType(ArtistType type);

    List<Artist> getAllByTypeOrderByName(ArtistType type);

    @Query("SELECT new com.romanpulov.odeonwss.dto.ArtistIdNameDTO( " +
            "ar.id," +
            "ar.name" +
            ") " +
            "FROM Artist as ar " +
            "ORDER BY ar.name")
    List<ArtistIdNameDTO> getAllIdName();

    Optional<Artist> findFirstByTypeAndName(ArtistType type, String name);

    Optional<Artist> findFirstByName(String name);

    Optional<Artist> findFirstByMigrationId(Long migrationId);
}
