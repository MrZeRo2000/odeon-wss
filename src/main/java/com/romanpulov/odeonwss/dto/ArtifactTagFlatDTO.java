package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public interface ArtifactTagFlatDTO extends BaseEntityDTO {
    Long getArtifactId();
    String getName();
}
