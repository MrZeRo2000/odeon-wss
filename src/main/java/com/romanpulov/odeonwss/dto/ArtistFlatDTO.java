package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.serializer.ArtistCategoryTypeDeserializer;
import com.romanpulov.odeonwss.serializer.ArtistCategoryTypeSerializer;
import com.romanpulov.odeonwss.serializer.ArtistTypeDeserializer;
import com.romanpulov.odeonwss.serializer.ArtistTypeSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

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
