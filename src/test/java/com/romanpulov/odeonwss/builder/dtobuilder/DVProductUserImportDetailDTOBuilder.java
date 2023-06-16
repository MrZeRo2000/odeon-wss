package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.DVProductUserImportDetailDTO;

public class DVProductUserImportDetailDTOBuilder extends AbstractClassBuilder<DVProductUserImportDetailDTO> {
    public DVProductUserImportDetailDTOBuilder() {
        super(DVProductUserImportDetailDTO.class);
    }

    public DVProductUserImportDetailDTO withTitle(String title) {
        this.instance.setTitle(title);
        return instance;
    }

    public DVProductUserImportDetailDTO withOriginalTitle(String originalTitle) {
        this.instance.setOriginalTitle(originalTitle);
        return instance;
    }

    public DVProductUserImportDetailDTO withYear(long year) {
        this.instance.setYear(year);
        return instance;
    }
}
