package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.serializer.ArtistCategoryTypeDeserializer;
import com.romanpulov.odeonwss.serializer.ArtistCategoryTypeSerializer;
import com.romanpulov.odeonwss.serializer.ArtistTypeDeserializer;
import com.romanpulov.odeonwss.serializer.ArtistTypeSerializer;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ArtistFlatDTO {
    Long getId();
    String getArtistName();
    @JsonSerialize(using = ArtistTypeSerializer.class)
    @JsonDeserialize(using = ArtistTypeDeserializer.class)
    ArtistType getArtistType();
    String getArtistTypeCode();
    @JsonSerialize(using = ArtistCategoryTypeSerializer.class)
    @JsonDeserialize(using = ArtistCategoryTypeDeserializer.class)
    ArtistCategoryType getCategoryType();
    String getCategoryTypeCode();
    String getCategoryName();
    Long getDetailId();
    String getArtistBiography();
    Long getHasLyrics();
}
