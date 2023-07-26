package com.romanpulov.odeonwss.mapper;

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
        Artist entity = new Artist();

        // immutable fields
        entity.setId(dto.getId());
        entity.setType(dto.getArtistType() == null? ArtistType.ARTIST : dto.getArtistType());

        // mutable fields
        this.update(entity, dto);

        return entity;
    }

    @Override
    public void update(Artist entity, ArtistDTO dto) {
        if (dto.getArtistType() != null) {
            entity.setType(dto.getArtistType());
        }
        entity.setName(dto.getArtistName());
    }
}
