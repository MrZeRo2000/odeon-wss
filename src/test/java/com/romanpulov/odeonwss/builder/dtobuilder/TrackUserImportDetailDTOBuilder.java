package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.user.TrackUserImportDetailDTO;

public class TrackUserImportDetailDTOBuilder extends AbstractClassBuilder<TrackUserImportDetailDTO> {
    public TrackUserImportDetailDTOBuilder() {
        super(TrackUserImportDetailDTO.class);
    }

    public TrackUserImportDetailDTOBuilder withTitle(String title) {
        this.instance.setTitle(title);
        return this;
    }

    public TrackUserImportDetailDTOBuilder withDuration(long duration) {
        this.instance.setDuration(duration);
        return this;
    }
}
