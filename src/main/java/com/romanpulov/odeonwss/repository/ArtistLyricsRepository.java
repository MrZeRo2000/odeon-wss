package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtistLyricsTableDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;
import com.romanpulov.odeonwss.view.TextView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ArtistLyricsRepository extends CrudRepository<ArtistLyrics, Long> {
    @Query("SELECT new com.romanpulov.odeonwss.dto.ArtistLyricsTableDTO (" +
            "al.id, " +
            "ar.name, " +
            "al.title " +
            ") " +
            "FROM ArtistLyrics al " +
            "INNER JOIN Artist ar ON al.artist = ar " +
            "ORDER BY ar.name, al.title")
    List<ArtistLyricsTableDTO> getArtistLyricsTableDTO();

    Optional<TextView> findArtistLyricsById(Long id);

    List<ArtistLyrics> findAllByArtistOrderByTitle(Artist artist);

    Optional<ArtistLyrics> findFirstByArtistAndTitle(Artist artist, String title);
}
