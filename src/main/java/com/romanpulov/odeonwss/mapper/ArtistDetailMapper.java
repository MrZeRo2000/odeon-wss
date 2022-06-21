package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistDetail;

public class ArtistDetailMapper {
    public static ArtistDetail createFromArtistCategoriesDetailDTO(Artist artist, ArtistCategoriesDetailDTO acd) {
        ArtistDetail artistDetail = new ArtistDetail();
        artistDetail.setArtist(artist);
        artistDetail.setBiography(acd.getArtistBiography());

        return artistDetail;
    }

    public static ArtistDetail updateFromArtistCategoriesDetailDTO(ArtistDetail artistDetail, ArtistCategoriesDetailDTO acd) {
        artistDetail.setBiography(acd.getArtistBiography());

        return artistDetail;
    }
}