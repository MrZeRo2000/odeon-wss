package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface DVProductFlatDTO {
    Long getId();
    Long getArtifactTypeId();
    Long getDvOriginId();
    String getDvOriginName();
    String getTitle();
    String getOriginalTitle();
    Long getYear();
    String getFrontInfo();
    String getDescription();
    String getNotes();
    Long getDvCategoryId();
    String getDvCategoryName();
}
