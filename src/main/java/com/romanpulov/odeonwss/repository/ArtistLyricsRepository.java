package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtistLyricsRepository extends CrudRepository<ArtistLyrics, Long> {
    List<ArtistLyrics> findAllByArtistOrderByTitle(Artist artist);
}
