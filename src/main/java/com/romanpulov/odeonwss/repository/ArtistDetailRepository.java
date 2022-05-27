package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistDetail;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ArtistDetailRepository extends CrudRepository<ArtistDetail, Long> {
    Optional<ArtistDetail> findArtistDetailByArtist(Artist artist);
}