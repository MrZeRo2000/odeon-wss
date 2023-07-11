package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = ArtifactDTOImpl.class)
public interface ArtifactDTO extends BaseEntityDTO {
    ArtifactTypeDTO getArtifactType();
    ArtistDTO getArtist();
    ArtistDTO getPerformerArtist();
    String getTitle();
    Long getYear();
    Long getDuration();
    Long getSize();
    LocalDateTime getInsertDateTime();
}
