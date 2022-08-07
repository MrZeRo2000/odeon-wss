package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.MediaFile;

public class EntityCompositionBuilder {
    private final Composition composition;

    public EntityCompositionBuilder() {
        composition = new Composition();
    }

    public EntityCompositionBuilder withArtifact(Artifact artifact) {
        composition.setArtifact(artifact);
        return this;
    }

    public EntityCompositionBuilder withArtist(Artist artist) {
        composition.setArtist(artist);
        return this;
    }

    public EntityCompositionBuilder withMediaFile(MediaFile mediaFile) {
        composition.getMediaFiles().add(mediaFile);
        return this;
    }


    public EntityCompositionBuilder withTitle(String title) {
        composition.setTitle(title);
        return this;
    }

    public EntityCompositionBuilder withDuration(long duration) {
        composition.setDuration(duration);
        return this;
    }

    public EntityCompositionBuilder withDiskNum(long diskNum) {
        composition.setDiskNum(diskNum);
        return this;
    }

    public EntityCompositionBuilder withNum(long num) {
        composition.setNum(num);
        return this;
    }

    public Composition build() {
        return composition;
    }
}
