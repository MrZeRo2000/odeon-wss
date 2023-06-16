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

    public DVProductUserImportDTO withArtifactTypeId(long artifactTypeId) {
        this.instance.setArtifactTypeId(artifactTypeId);
        return instance;
    }

    public DVProductUserImportDTO withDvOriginId(long dvOriginId) {
        this.instance.setDvOriginId(dvOriginId);
        return instance;
    }

    public DVProductUserImportDTO withDvProductDetails(List<DVProductUserImportDetailDTO> dvProductDetails) {
        this.instance.setDvProductDetails(dvProductDetails);
        return instance;
    }

    public DVProductUserImportDTO withFrontInfo(String frontInfo) {
        this.instance.setFrontInfo(frontInfo);
        return instance;
    }

    public DVProductUserImportDTO withDvCategories(List<DVCategoryDTO> dvCategories) {
        this.instance.setDvCategories(dvCategories);
        return instance;
    }
}
