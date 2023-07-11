package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtistLyricsDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;
import org.springframework.stereotype.Component;

@Component
public class ArtistLyricsMapper implements EntityDTOMapper<ArtistLyrics, ArtistLyricsDTO> {
    @Override
    public String getEntityName() {
        return "ArtistLyrics";
    }

    @Override
    public ArtistLyrics fromDTO(ArtistLyricsDTO dto) {
        ArtistLyrics artistLyrics = new ArtistLyrics();

        artistLyrics.setId(dto.getId());

        Artist artist = new Artist();
        artist.setId(dto.getArtistId());
        artistLyrics.setArtist(artist);

        artistLyrics.setTitle(dto.getTitle());
        artistLyrics.setText(dto.getText());

        return artistLyrics;
    }

    @Override
    public void update(ArtistLyrics entity, ArtistLyricsDTO dto) {
        Artist artist = new Artist();
        artist.setId(dto.getArtistId());
        entity.setArtist(artist);

        entity.setTitle(dto.getTitle());
        entity.setText(dto.getText());
    }
}
