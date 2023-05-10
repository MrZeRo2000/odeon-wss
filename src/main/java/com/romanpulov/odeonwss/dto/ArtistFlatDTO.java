package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ArtistFlatDTO {
    Long getId();
    String getArtistName();
    ArtistType getArtistType();
    ArtistCategoryType getCategoryType();
    String getCategoryName();
    Long getDetailId();
}
