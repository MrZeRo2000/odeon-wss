package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.dto.user.TrackUserImportDTO;

import java.util.List;

public class TrackUserImportDTOBuilder extends AbstractClassBuilder<TrackUserImportDTO> {
    public TrackUserImportDTOBuilder() {
        super(TrackUserImportDTO.class);
    }

    public TrackUserImportDTOBuilder withArtifact(ArtifactDTO artifact) {
        this.instance.setArtifact(artifact);
        return this;
    }

    public TrackUserImportDTOBuilder withMediaFile(MediaFileDTO mediaFile) {
        this.instance.setMediaFile(mediaFile);
        return this;
    }

    public TrackUserImportDTOBuilder withDVType(IdNameDTO dvType) {
        this.instance.setDvType(dvType);
        return this;
    }

    public TrackUserImportDTOBuilder withNum(long num) {
        this.instance.setNum(num);
        return this;
    }

    public TrackUserImportDTOBuilder withTitles(List<String> titles) {
        this.instance.setTitles(titles);
        return this;
    }

    public TrackUserImportDTOBuilder withChapters(List<String> chapters) {
        this.instance.setChapters(chapters);
        return this;
    }

    public TrackUserImportDTOBuilder withArtists(List<String> artists) {
        this.instance.setArtists(artists);
        return this;
    }
}
