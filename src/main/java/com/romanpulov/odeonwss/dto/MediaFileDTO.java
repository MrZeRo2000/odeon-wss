package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = MediaFileDTOImpl.class)
public interface MediaFileDTO extends BaseEntityDTO {
    Long getArtifactId();
    String getArtifactTitle();
    String getName();
    String getFormat();
    Long getSize();
    Long getBitrate();
    Long getDuration();
    Long getWidth();
    Long getHeight();
    Long getHasExtra();
    String getExtra();
}
