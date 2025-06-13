package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface TrackFlatDTO {
    Long getId();
    Long getArtifactTypeId();
    String getArtifactTypeName();
    Long getArtifactId();
    String getArtifactTitle();
    Long getArtifactYear();
    Long getArtifactDuration();
    Long getArtifactArtistId();
    String getArtifactArtistName();
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
    Long getMediaFileId();
    String getMediaFileName();
    Long getDvProductId();
    String getDvProductTitle();
    String getTagName();
}
