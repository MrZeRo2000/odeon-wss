package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.TrackDTOImpl;

import java.util.Collection;
import java.util.stream.Collectors;

public class TrackDTOBuilder extends AbstractClassBuilder<TrackDTOImpl> {
    public TrackDTOBuilder() {
        super(TrackDTOImpl.class);
    }

    public TrackDTOBuilder withArtifactId(long artifactId) {
        instance.setArtifact(new ArtifactDTOBuilder().withId(artifactId).build());
        return this;
    }

    public TrackDTOBuilder withArtistId(long artistId) {
        instance.setArtist(new ArtistDTOBuilder().withId(artistId).build());
        return this;
    }

    public TrackDTOBuilder withPerformerArtistId(long performerArtistId) {
        instance.setPerformerArtist(new ArtistDTOBuilder().withId(performerArtistId).build());
        return this;
    }

    public TrackDTOBuilder withDvTypeId(long dvTypeId) {
        instance.setDvType(new IdNameDTOBuilder().withId(dvTypeId).build());
        return this;
    }

    public TrackDTOBuilder withTitle(String title) {
        instance.setTitle(title);
        return this;
    }

    public TrackDTOBuilder withDuration(long duration) {
        instance.setDuration(duration);
        return this;
    }

    public TrackDTOBuilder withDiskNum(long diskNum) {
        instance.setDiskNum(diskNum);
        return this;
    }

    public TrackDTOBuilder withNum(long num) {
        instance.setNum(num);
        return this;
    }

    public TrackDTOBuilder withMediaFileIds(Collection<Long> mediaFileIds) {
        instance.setMediaFiles(mediaFileIds
                .stream()
                .map(v -> new MediaFileDTOBuilder().withId(v).build())
                .collect(Collectors.toList()));
        return this;
    }

    public TrackDTOBuilder withDvProductId(long dvProductId) {
        instance.setDvProduct(new DVProductDTOBuilder().withId(dvProductId).build());
        return this;
    }
}
