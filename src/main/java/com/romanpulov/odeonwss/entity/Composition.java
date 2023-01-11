package com.romanpulov.odeonwss.entity;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "compositions")
@AttributeOverride(name = "id", column = @Column(name = "comp_id"))
@AttributeOverride(name = "migrationId", column = @Column(name = "comp_migration_id"))
public class Composition extends AbstractBaseMigratedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artf_id", referencedColumnName = "artf_id")
    @NotNull
    private Artifact artifact;

    public Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arts_id", referencedColumnName = "arts_id")
    @org.springframework.lang.Nullable
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
    @org.springframework.lang.Nullable
    private Artist performerArtist;

    @Nullable
    public Artist getPerformerArtist() {
        return performerArtist;
    }

    public void setPerformerArtist(@org.springframework.lang.Nullable Artist performerArtist) {
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

    @Column(name = "comp_title")
    @NotNull
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "comp_duration")
    @Nullable
    private Long duration;

    @Nullable
    public Long getDuration() {
        return duration;
    }

    public void setDuration(@Nullable Long duration) {
        this.duration = duration;
    }

    @Column(name = "comp_disk_num")
    @Nullable
    private Long diskNum;

    @Nullable
    public Long getDiskNum() {
        return diskNum;
    }

    public void setDiskNum(@Nullable Long diskNum) {
        this.diskNum = diskNum;
    }

    @Column(name = "comp_num")
    @Nullable
    private Long num;

    @Nullable
    public Long getNum() {
        return num;
    }

    public void setNum(@Nullable Long num) {
        this.num = num;
    }

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(name = "compositions_media_files",
            joinColumns = @JoinColumn(name = "comp_id"),
            inverseJoinColumns = @JoinColumn(name = "mdfl_id")
    )
    private Set<MediaFile> mediaFiles = new HashSet<>();

    public Set<MediaFile> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(Set<MediaFile> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Composition that = (Composition) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Composition{" +
                "id=" + getId() +
                ", artifact=" + artifact +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", diskNum=" + diskNum +
                ", num=" + num +
                '}';
    }
}
