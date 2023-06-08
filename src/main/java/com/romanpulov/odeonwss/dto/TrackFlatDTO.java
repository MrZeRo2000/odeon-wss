package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface TrackFlatDTO {
    Long getId();
    Long getArtifactId();
    Long getArtistId();
    String getArtistName();
    Long getPerformerArtistId();
    String getPerformerArtistName();
    Long getDvTypeId();
    String getDvTypeName();
    String getTitle();
    Long getDuration();
    Long getDiskNum();
    Long getNum();
    Long getSize();
    Long getBitRate();
    String getFileName();
}
