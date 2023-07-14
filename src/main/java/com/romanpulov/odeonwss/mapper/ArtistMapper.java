package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.dto.ArtistDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper implements EntityDTOMapper<Artist, ArtistDTO> {
    @Override
    public String getEntityName() {
        return "Artist";
    }

    @Override
    public Artist fromDTO(ArtistDTO dto) {
        Artist artist = new Artist();
        artist.setType(
                dto.getArtistType() == null ?
                        ArtistType.ARTIST :
                        dto.getArtistType());
        artist.setName(dto.getArtistName());

        return artist;
    }

    @Override
    public void update(Artist entity, ArtistDTO dto) {
        entity.setName(dto.getArtistName());
        if (dto.getArtistType() != null) {
            entity.setType(dto.getArtistType());
        }
    }

    public Artist fromDTO(ArtistCategoriesDetailDTO dto) {
        Artist artist = new Artist();
        artist.setType(dto.getArtistType() == null? ArtistType.ARTIST : dto.getArtistType());
        artist.setName(dto.getArtistName());

        return artist;
    }

    public Artist update(Artist artist, ArtistCategoriesDetailDTO dto) {
        artist.setName(dto.getArtistName());
        if (dto.getArtistType() != null) {
            artist.setType(dto.getArtistType());
        }

        return artist;
    }
}
