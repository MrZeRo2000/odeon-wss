package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.user.DVProductUserImportDetailDTO;

public class DVProductUserImportDetailDTOBuilder extends AbstractClassBuilder<DVProductUserImportDetailDTO> {
    public DVProductUserImportDetailDTOBuilder() {
        super(DVProductUserImportDetailDTO.class);
    }

    public DVProductUserImportDetailDTOBuilder withTitle(String title) {
        this.instance.setTitle(title);
        return this;
    }

    public DVProductUserImportDetailDTOBuilder withOriginalTitle(String originalTitle) {
        this.instance.setOriginalTitle(originalTitle);
        return this;
    }

    public DVProductUserImportDetailDTOBuilder withYear(long year) {
        this.instance.setYear(year);
        return this;
    }
}
