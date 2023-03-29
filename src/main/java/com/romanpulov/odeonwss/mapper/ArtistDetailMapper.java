package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistDetail;
import org.springframework.stereotype.Component;

@Component
public class ArtistDetailMapper {
    public ArtistDetail fromArtistCategoriesDetailDTO(Artist artist, ArtistCategoriesDetailDTO dto) {
        ArtistDetail artistDetail = new ArtistDetail();
        artistDetail.setArtist(artist);
        artistDetail.setBiography(dto.getArtistBiography());

        return artistDetail;
    }

    public ArtistDetail update(ArtistDetail artistDetail, ArtistCategoriesDetailDTO dto) {
        artistDetail.setBiography(dto.getArtistBiography());

        return artistDetail;
    }
}
