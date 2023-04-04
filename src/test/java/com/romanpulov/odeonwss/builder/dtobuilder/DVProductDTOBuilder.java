package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.DVCategoryDTO;
import com.romanpulov.odeonwss.dto.DVOriginDTO;
import com.romanpulov.odeonwss.dto.DVProductDTOImpl;

import java.util.List;

public class DVProductDTOBuilder extends AbstractClassBuilder<DVProductDTOImpl> {
    public DVProductDTOBuilder() {
        super(DVProductDTOImpl.class);
    }

    public DVProductDTOBuilder withId(Long id) {
        this.instance.setId(id);
        return this;
    }

    public DVProductDTOBuilder withArtifactTypeId(Long artifactTypeId) {
        this.instance.setArtifactTypeId(artifactTypeId);
        return this;
    }

    public DVProductDTOBuilder withDvOrigin(DVOriginDTO dvOriginDTO) {
        this.instance.setDvOrigin(dvOriginDTO);
        return this;
    }

    public DVProductDTOBuilder withTitle(String title) {
        this.instance.setTitle(title);
        return this;
    }

    public DVProductDTOBuilder withOriginalTitle(String originalTitle) {
        this.instance.setOriginalTitle(originalTitle);
        return this;
    }

    public DVProductDTOBuilder withYear(Long year) {
        this.instance.setYear(year);
        return this;
    }

    public DVProductDTOBuilder withFrontInfo(String frontInfo) {
        this.instance.setFrontInfo(frontInfo);
        return this;
    }

    public DVProductDTOBuilder withDescription(String description) {
        this.instance.setDescription(description);
        return this;
    }

    public DVProductDTOBuilder withNotes(String notes) {
        this.instance.setNotes(notes);
        return this;
    }

    public DVProductDTOBuilder withDvCategories(List<DVCategoryDTO> dvCategories) {
        this.instance.setDvCategories(dvCategories);
        return this;
    }
}
