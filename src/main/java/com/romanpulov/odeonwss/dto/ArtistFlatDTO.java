package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ArtistFlatDTO {
    Long getId();
    String getArtistName();
    String getArtistType();
    String getCategoryType();
    String getCategoryName();
    Long getDetailId();
    Long getHasLyrics();
}
