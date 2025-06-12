package com.romanpulov.odeonwss.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tracks")
@AttributeOverride(name = "id", column = @Column(name = "trck_id"))
@AttributeOverride(name = "insertDateTime", column = @Column(name = "trck_ins_datm"))
@AttributeOverride(name = "updateDateTime", column = @Column(name = "trck_upd_datm"))
@AttributeOverride(name = "migrationId", column = @Column(name = "trck_migration_id"))
public class Track extends AbstractBaseMigratedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artf_id", referencedColumnName = "artf_id")
    @NotNull
    private Artifact artifact;

    public @NotNull Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(@NotNull Artifact artifact) {
        this.artifact = artifact;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arts_id", referencedColumnName = "arts_id")
    @Nullable
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
    @Nullable
    private Artist performerArtist;

    @Nullable
    public Artist getPerformerArtist() {
        return performerArtist;
    }

    public void setPerformerArtist(@Nullable Artist performerArtist) {
        this.performerArtist = performerArtist;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dvtp_id", referencedColumnName = "dvtp_id")
    @Nullable
    private DVType dvType;

    @Nullable
    public DVType getDvType() {
        return dvType;
    }

    public void setDvType(@Nullable DVType dvType) {
        this.dvType = dvType;
    }

    @Column(name = "trck_title")
    @NotNull
    private String title;

    public @NotNull String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    @Column(name = "trck_duration")
    @Nullable
    private Long duration;

    @Nullable
    public Long getDuration() {
        return duration;
    }

    public void setDuration(@Nullable Long duration) {
        this.duration = duration;
    }

    @Column(name = "trck_disk_num")
    @Nullable
    private Long diskNum;

    @Nullable
    public Long getDiskNum() {
        return diskNum;
    }

    public void setDiskNum(@Nullable Long diskNum) {
        this.diskNum = diskNum;
    }

    @Column(name = "trck_num")
    @Nullable
    private Long num;

    @Nullable
    public Long getNum() {
        return num;
    }

    public void setNum(@Nullable Long num) {
        this.num = num;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(name = "tracks_media_files",
            joinColumns = @JoinColumn(name = "trck_id"),
            inverseJoinColumns = @JoinColumn(name = "mdfl_id")
    )
    private Set<MediaFile> mediaFiles = new HashSet<>();

    public Set<MediaFile> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(Set<MediaFile> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(name = "tracks_dv_products",
            joinColumns = @JoinColumn(name = "trck_id"),
            inverseJoinColumns = @JoinColumn(name = "dvpd_id")
    )
    private Set<DVProduct> dvProducts = new HashSet<>();

    public Set<DVProduct> getDvProducts() {
        return dvProducts;
    }

    public void setDvProducts(Set<DVProduct> dvProducts) {
        this.dvProducts = dvProducts;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(name = "tracks_tags",
            joinColumns = @JoinColumn(name = "trck_id"),
            inverseJoinColumns = @JoinColumn(name = "ttag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track that = (Track) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + getId() +
                ", artifact="  + (Hibernate.isInitialized(artifact) ? artifact : "not initialized") +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", diskNum=" + diskNum +
                ", num=" + num +
                ", dvProducts=" + (Hibernate.isInitialized(dvProducts) ? dvProducts : "not initialized") +
                ", tags=" + (Hibernate.isInitialized(tags) ? tags : "not initialized") +
                '}';
    }
}
