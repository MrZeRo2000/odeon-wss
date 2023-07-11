package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = ArtifactTypeDTOImpl.class)
public interface ArtifactTypeDTO extends BaseEntityDTO {
    String getName();
}
