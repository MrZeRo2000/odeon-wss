package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.serializer.ArtistTypeDeserializer;
import com.romanpulov.odeonwss.serializer.ArtistTypeSerializer;

import java.time.LocalDateTime;

public interface ArtifactFlatDTO {
    Long getId();
    Long getArtifactTypeId();
    String getArtifactTypeName();
    @JsonSerialize(using = ArtistTypeSerializer.class)
    @JsonDeserialize(using = ArtistTypeDeserializer.class)
    ArtistType getArtistType();
    Long getArtistId();
    String getArtistName();
    Long getPerformerArtistId();
    String getPerformerArtistName();
    String getTitle();
    Long getYear();
    Long getDuration();
    Long getSize();
    LocalDateTime getInsertDateTime();
}
