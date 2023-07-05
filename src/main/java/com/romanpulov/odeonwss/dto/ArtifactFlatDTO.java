package com.romanpulov.odeonwss.dto;

import java.time.LocalDateTime;

public interface ArtifactFlatDTO {
    Long getId();
    Long getArtifactTypeId();
    String getArtifactTypeName();
    String getArtistTypeCode();
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
