package com.romanpulov.odeonwss.entity;

import org.hibernate.Hibernate;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "artifacts")
@AttributeOverride(name = "id", column = @Column(name = "artf_id"))
@AttributeOverride(name = "insertDateTime", column = @Column(name = "artf_ins_datm"))
@AttributeOverride(name = "updateDateTime", column = @Column(name = "artf_upd_datm"))
@AttributeOverride(name = "migrationId", column = @Column(name = "artf_migration_id"))
public class Artifact extends AbstractBaseMigratedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attp_id", referencedColumnName = "attp_id")
    @NotNull
    private ArtifactType artifactType;

    public @NotNull ArtifactType getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(@NotNull ArtifactType artifactType) {
        this.artifactType = artifactType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arts_id", referencedColumnName = "arts_id")
    private Artist artist;

    @Nullable
    public Artist getArtist() {
        return artist;
    }

    public void setArtist(@Nullable Artist artist) {
        this.artist = artist;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perf_arts_id", referencedColumnName = "arts_id")
    private Artist performerArtist;

    @Nullable
    public Artist getPerformerArtist() {
        return performerArtist;
    }

    public void setPerformerArtist(@Nullable Artist performerArtist) {
        this.performerArtist = performerArtist;
    }

    @Column(name = "artf_title")
    @NotNull
    private String title;

    public @NotNull String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    @Column(name = "artf_year")
    @Nullable
    private Long year;

    @Nullable
    public Long getYear() {
        return year;
    }

    public void setYear(@Nullable Long year) {
        this.year = year;
    }

    @Column(name = "artf_duration")
    @Nullable
    private Long duration;

    @Nullable
    public Long getDuration() {
        return duration;
    }

    public void setDuration(@Nullable Long duration) {
        this.duration = duration;
    }

    @Column(name = "artf_size")
    @Nullable
    private Long size;

    @Nullable
    public Long getSize() {
        return size;
    }

    public void setSize(@Nullable Long size) {
        this.size = size;
    }

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "artifact", fetch = FetchType.LAZY)
    private List<Track> tracks;

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "artifact", fetch = FetchType.LAZY)
    private List<MediaFile> mediaFiles;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "artifact", fetch = FetchType.LAZY)
    private List<ArtifactTag> artifactTags;

    public List<ArtifactTag> getArtifactTags() {
        return artifactTags;
    }

    public void setArtifactTags(List<ArtifactTag> artifactTags) {
        this.artifactTags = artifactTags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artifact artifact = (Artifact) o;
        return getId().equals(artifact.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "id=" + getId() +
                ", artifactType=" + (Hibernate.isInitialized(artifactType) ? artifactType : "not initialized") +
                ", artist=" + (Hibernate.isInitialized(artist) ? artist : "not initialized") +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", duration=" + duration +
                ", size=" + size +
                ", insertDateTime=" + getInsertDateTime() +
                ", tracks=" + (Hibernate.isInitialized(tracks) ? tracks : "not initialized") +
                ", mediaFiles=" + (Hibernate.isInitialized(mediaFiles) ? mediaFiles : "not initialized") +
                '}';
    }
}
