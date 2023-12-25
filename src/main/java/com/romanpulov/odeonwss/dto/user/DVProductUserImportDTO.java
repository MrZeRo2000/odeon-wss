package com.romanpulov.odeonwss.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.dto.DVCategoryDTO;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DVProductUserImportDTO {
    private Long artifactTypeId;
    private Long dvOriginId;
    List<DVProductUserImportDetailDTO> dvProductDetails = new ArrayList<>();
    String frontInfo;
    List<DVCategoryDTO> dvCategories = new ArrayList<>();

    public Long getArtifactTypeId() {
        return artifactTypeId;
    }

    public void setArtifactTypeId(Long artifactTypeId) {
        this.artifactTypeId = artifactTypeId;
    }

    public Long getDvOriginId() {
        return dvOriginId;
    }

    public void setDvOriginId(Long dvOriginId) {
        this.dvOriginId = dvOriginId;
    }

    public List<DVProductUserImportDetailDTO> getDVProductDetails() {
        return dvProductDetails;
    }

    public void setDvProductDetails(List<DVProductUserImportDetailDTO> dvProductDetails) {
        this.dvProductDetails = dvProductDetails;
    }

    public String getFrontInfo() {
        return frontInfo;
    }

    public void setFrontInfo(String frontInfo) {
        this.frontInfo = frontInfo;
    }

    public List<DVCategoryDTO> getDvCategories() {
        return dvCategories;
    }

    public void setDvCategories(List<DVCategoryDTO> dvCategories) {
        this.dvCategories = dvCategories;
    }

    @Override
    public String toString() {
        return "DVProductUserImportDTO{" +
                "artifactTypeId=" + artifactTypeId +
                ", dvOriginId=" + dvOriginId +
                ", dvProductDetails=" + dvProductDetails +
                ", frontInfo='" + frontInfo + '\'' +
                ", dvCategories=" + dvCategories +
                ", DVProductDetails=" + getDVProductDetails() +
                '}';
    }
}
