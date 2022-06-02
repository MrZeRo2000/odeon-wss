package com.romanpulov.odeonwss.mapper;


import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.entity.Artist;

public class ArtistMapper {
    public static Artist fromArtistCategoriesDetailDTO(ArtistCategoriesDetailDTO acd) {
        Artist artist = new Artist();
        artist.setType(acd.getArtistType());
        artist.setName(acd.getArtistName());

        return artist;
    }
}
