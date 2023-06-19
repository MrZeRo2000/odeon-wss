package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.DVCategoryDTO;
import com.romanpulov.odeonwss.dto.DVProductUserImportDTO;
import com.romanpulov.odeonwss.dto.DVProductUserImportDetailDTO;

import java.util.List;

public class DVProductUserImportDTOBuilder extends AbstractClassBuilder<DVProductUserImportDTO> {
    public DVProductUserImportDTOBuilder() {
        super(DVProductUserImportDTO.class);
    }

    public DVProductUserImportDTOBuilder withArtifactTypeId(long artifactTypeId) {
        this.instance.setArtifactTypeId(artifactTypeId);
        return this;
    }

    public DVProductUserImportDTOBuilder withDvOriginId(long dvOriginId) {
        this.instance.setDvOriginId(dvOriginId);
        return this;
    }

    public DVProductUserImportDTOBuilder withDvProductDetails(List<DVProductUserImportDetailDTO> dvProductDetails) {
        this.instance.setDvProductDetails(dvProductDetails);
        return this;
    }

    public DVProductUserImportDTOBuilder withFrontInfo(String frontInfo) {
        this.instance.setFrontInfo(frontInfo);
        return this;
    }

    public DVProductUserImportDTOBuilder withDvCategories(List<DVCategoryDTO> dvCategories) {
        this.instance.setDvCategories(dvCategories);
        return this;
    }
}
