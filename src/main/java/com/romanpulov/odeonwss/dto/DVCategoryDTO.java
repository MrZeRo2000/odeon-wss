package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = DVCategoryDTOImpl.class)
public interface DVCategoryDTO extends BaseEntityDTO {
    String getName();
}
