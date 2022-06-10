package com.romanpulov.odeonwss.mapper;


import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;

public class ArtistMapper {
    public static Artist createFromArtistCategoriesDetailDTO(ArtistCategoriesDetailDTO acd) {
        Artist artist = new Artist();
        artist.setType(acd.getArtistType() == null? ArtistType.ARTIST : acd.getArtistType());
        artist.setName(acd.getArtistName());

        return artist;
    }

    public static Artist updateFromArtistCategoriesDetailDTO(Artist artist, ArtistCategoriesDetailDTO acd) {
        artist.setName(acd.getArtistName());
        if (acd.getArtistType() != null) {
            artist.setType(acd.getArtistType());
        }

        return artist;
    }
}
