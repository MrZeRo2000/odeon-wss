package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.romanpulov.odeonwss.dto.serializer.ArtistTypeDeserializer;
import com.romanpulov.odeonwss.dto.serializer.ArtistTypeSerializer;
import com.romanpulov.odeonwss.entity.ArtistType;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = ArtistDTOImpl.class)
public interface ArtistDTO extends AbstractEntityDTO {
    String getArtistName();
    @JsonSerialize(using = ArtistTypeSerializer.class)
    @JsonDeserialize(using = ArtistTypeDeserializer.class)
    ArtistType getArtistType();
    String getGenre();
    List<String> getStyles();
    Long getDetailId();
}
