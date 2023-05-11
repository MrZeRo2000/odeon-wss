package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = ArtistDTOImpl.class)
public interface ArtistDTO extends AbstractEntityDTO {
    String getArtistName();
    String getArtistType();
    String getGenre();
    List<String> getStyles();
    Long getDetailId();
    Boolean getHasLyrics();
}
