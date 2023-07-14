package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.converter.ArtistTypeConverter;
import com.romanpulov.odeonwss.serializer.ArtistTypeDeserializer;
import com.romanpulov.odeonwss.serializer.ArtistTypeSerializer;
import jakarta.persistence.Convert;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = ArtistDTOImpl.class)
public interface ArtistDTO extends BaseEntityDTO {
    String getArtistName();
    @JsonSerialize(using = ArtistTypeSerializer.class)
    @JsonDeserialize(using = ArtistTypeDeserializer.class)
    @Convert(converter = ArtistTypeConverter.class)
    ArtistType getArtistType();
    String getGenre();
    List<String> getStyles();
    Long getDetailId();
    String getArtistBiography();
    Boolean getHasLyrics();
}
