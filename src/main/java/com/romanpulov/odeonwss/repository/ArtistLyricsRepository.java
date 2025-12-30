package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtistLyricsDTO;
import com.romanpulov.odeonwss.dto.TextDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional(readOnly = true)
public interface ArtistLyricsRepository extends EntityDTORepository<ArtistLyrics, ArtistLyricsDTO> {
    @Query("SELECT " +
            "al.id AS id, " +
            "ar.id AS artistId, " +
            "ar.name AS artistName, " +
            "al.title AS title, " +
            "al.text AS text " +
            "FROM ArtistLyrics al " +
            "INNER JOIN Artist ar ON al.artist = ar " +
            "WHERE al.id = :id")
    Optional<ArtistLyricsDTO> findDTOById(long id);

    @Query("SELECT " +
            "al.id AS id, " +
            "ar.name AS artistName, " +
            "al.title AS title " +
            "FROM ArtistLyrics al " +
            "INNER JOIN Artist ar ON al.artist = ar " +
            "ORDER BY ar.name, al.title")
    List<ArtistLyricsDTO> findAllDTO();

    @Query("SELECT " +
            "al.id AS id, " +
            "ar.name AS artistName, " +
            "al.title AS title " +
            "FROM ArtistLyrics al " +
            "INNER JOIN Artist ar ON al.artist = ar " +
            "WHERE ar.id = :artistId " +
            "ORDER BY al.title")
    List<ArtistLyricsDTO> findAllDTOByArtistId(Long artistId);

    Optional<TextDTO> findArtistLyricsById(Long id);

    Optional<ArtistLyrics> findFirstByArtistAndTitle(Artist artist, String title);

    @Query("SELECT DISTINCT(al.artist.id) FROM ArtistLyrics al")
    Set<Long> findDistinctArtistId();
}
