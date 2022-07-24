package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtistLyricsEditDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;

public class ArtistLyricsMapper {
    public static ArtistLyrics fromEditDTO(ArtistLyricsEditDTO editDTO) {
        ArtistLyrics artistLyrics = new ArtistLyrics();

        artistLyrics.setId(editDTO.getId());

        Artist artist = new Artist();
        artist.setId(editDTO.getArtistId());
        artistLyrics.setArtist(artist);

        artistLyrics.setTitle(editDTO.getTitle());
        artistLyrics.setText(editDTO.getText());

        return artistLyrics;
    }

    public static ArtistLyrics updateFromEditDTO(ArtistLyrics entity, ArtistLyricsEditDTO editDTO, Artist artist) {
        entity.setArtist(artist);
        entity.setTitle(editDTO.getTitle());
        entity.setText(editDTO.getText());

        return entity;
    }
}
