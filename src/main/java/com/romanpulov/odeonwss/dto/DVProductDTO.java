package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = DVProductDTOImpl.class)
public interface DVProductDTO extends AbstractEntityDTO {
    Long getArtifactTypeId();
    DVOriginDTO getDvOrigin();
    String getTitle();
    String getOriginalTitle();
    Long getYear();
    String getFrontInfo();
    String getDescription();
    Boolean getHasDescription();
    String getNotes();
    Boolean getHasNotes();
    List<DVCategoryDTO> getDvCategories();
}
