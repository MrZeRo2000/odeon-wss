package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.*;

public class EntityTrackBuilder extends AbstractClassBuilder<Track> {

    public EntityTrackBuilder() {
        super(Track.class);
    }

    public EntityTrackBuilder withArtifact(Artifact artifact) {
        instance.setArtifact(artifact);
        return this;
    }

    public EntityTrackBuilder withArtist(Artist artist) {
        instance.setArtist(artist);
        return this;
    }

    public EntityTrackBuilder withPerformerArtist(Artist performerArtist) {
        instance.setPerformerArtist(performerArtist);
        return this;
    }

    public EntityTrackBuilder withMediaFile(MediaFile mediaFile) {
        instance.getMediaFiles().add(mediaFile);
        return this;
    }

    public EntityTrackBuilder withTitle(String title) {
        instance.setTitle(title);
        return this;
    }

    public EntityTrackBuilder withDuration(long duration) {
        instance.setDuration(duration);
        return this;
    }

    public EntityTrackBuilder withDiskNum(long diskNum) {
        instance.setDiskNum(diskNum);
        return this;
    }

    public EntityTrackBuilder withNum(long num) {
        instance.setNum(num);
        return this;
    }

    public EntityTrackBuilder withDvType(DVType dvType) {
        instance.setDvType(dvType);
        return this;
    }

    public EntityTrackBuilder withMigrationId(long migrationId) {
        instance.setMigrationId(migrationId);
        return this;
    }
}
