package com.romanpulov.odeonwss.builder.dtobuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.dto.TrackEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.MediaFile;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class TrackEditDTOBuilder extends AbstractClassBuilder<TrackEditDTO> {
    public TrackEditDTOBuilder() {
        super(TrackEditDTO.class);
    }

    public TrackEditDTOBuilder withArtifactId(long artifactId) {
        instance.setArtifactId(artifactId);
        return this;
    }

    public TrackEditDTOBuilder withArtistId(long artistId) {
        instance.setArtistId(artistId);
        return this;
    }

    public TrackEditDTOBuilder withPerformerArtistId(long performerArtistId) {
        instance.setPerformerArtistId(performerArtistId);
        return this;
    }

    public TrackEditDTOBuilder withDvTypeId(long dvTypeId) {
        instance.setDvTypeId(dvTypeId);
        return this;
    }

    public TrackEditDTOBuilder withTitle(String title) {
        instance.setTitle(title);
        return this;
    }

    public TrackEditDTOBuilder withDuration(long duration) {
        instance.setDuration(duration);
        return this;
    }

    public TrackEditDTOBuilder withDiskNum(long diskNum) {
        instance.setDiskNum(diskNum);
        return this;
    }

    public TrackEditDTOBuilder withNum(long num) {
        instance.setNum(num);
        return this;
    }

    public TrackEditDTOBuilder withMediaFileIds(Collection<Long> mediaFileIds) {
        instance.setMediaFiles(new HashSet<>(mediaFileIds));
        return this;
    }
}
