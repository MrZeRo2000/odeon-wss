package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = ArtistLyricsDTO.class)
public interface ArtistLyricsDTO extends BaseEntityDTO {
    Long getArtistId();
    String getArtistName();
    String getTitle();
    String getText();
}
