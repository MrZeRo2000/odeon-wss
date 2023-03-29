package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtistLyricsEditDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;
import org.springframework.stereotype.Component;

@Component
public class ArtistLyricsMapper {
    public ArtistLyrics fromDTO(ArtistLyricsEditDTO dto) {
        ArtistLyrics artistLyrics = new ArtistLyrics();

        artistLyrics.setId(dto.getId());

        Artist artist = new Artist();
        artist.setId(dto.getArtistId());
        artistLyrics.setArtist(artist);

        artistLyrics.setTitle(dto.getTitle());
        artistLyrics.setText(dto.getText());

        return artistLyrics;
    }

    public ArtistLyrics update(ArtistLyrics entity, ArtistLyricsEditDTO dto, Artist artist) {
        entity.setArtist(artist);
        entity.setTitle(dto.getTitle());
        entity.setText(dto.getText());

        return entity;
    }
}
