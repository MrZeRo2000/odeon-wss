package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;
import org.springframework.data.repository.CrudRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ArtistLyricsRepository extends CrudRepository<ArtistLyrics, Long> {
    List<ArtistLyrics> findAllByArtistOrderByTitle(Artist artist);

    Optional<ArtistLyrics> findFirstByArtistAndTitle(Artist artist, String title);
}
