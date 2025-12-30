package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Value;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = ArtistLyricsDTOImpl.class)
public interface ArtistLyricsDTO extends BaseEntityDTO {
    @Override
    @Value("#{target.id}") // Explicitly map the 'id' alias from your JPQL
    Long getId();

    Long getArtistId();
    String getArtistName();
    String getTitle();
    String getText();
}
