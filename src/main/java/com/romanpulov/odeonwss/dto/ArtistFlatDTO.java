package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ArtistFlatDTO {
    Long getId();
    String getArtistName();
    ArtistType getArtistType();
    String getArtistTypeCode();
    ArtistCategoryType getCategoryType();
    String getCategoryTypeCode();
    String getCategoryName();
    Long getDetailId();
    String getArtistBiography();
    Long getHasLyrics();
}
