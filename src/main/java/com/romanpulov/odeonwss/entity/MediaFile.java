package com.romanpulov.odeonwss.entity;

import org.hibernate.Hibernate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "media_files")
@AttributeOverride(name = "id", column = @Column(name = "mdfl_id"))
@AttributeOverride(name = "insertDateTime", column = @Column(name = "mdfl_ins_datm"))
@AttributeOverride(name = "updateDateTime", column = @Column(name = "mdfl_upd_datm"))
@AttributeOverride(name = "migrationId", column = @Column(name = "mdfl_migration_id"))
public class MediaFile extends AbstractBaseMigratedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artf_id", referencedColumnName = "artf_id")
    private Artifact artifact;

    public Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    @ManyToMany(mappedBy = "mediaFiles")
    private Set<Track> tracks = new HashSet<>();

    public Set<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }

    @Column(name = "mdfl_name")
    @NotNull
    private String name;

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Column(name = "mdfl_format_code")
    private String format;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Column(name = "mdfl_size")
    private Long size;

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Column(name = "mdfl_bitrate")
    private Long bitrate;

    public Long getBitrate() {
        return bitrate;
    }

    public void setBitrate(Long bitrate) {
        this.bitrate = bitrate;
    }

    @Column(name = "mdfl_duration")
    private Long duration;

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Column(name = "mdfl_width")
    private Long width;

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    @Column(name = "mdfl_height")
    private Long height;

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    @Column(name = "mdfl_extra")
    private String extra;

    public Long getExtra() {
        return height;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public MediaFile() {
    }

    public static MediaFile fromId(Long id) {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(id);

        return mediaFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaFile mediaFile = (MediaFile) o;
        return getId().equals(mediaFile.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "MediaFile{" +
                "id=" + getId() +
                ", artifact=" + (Hibernate.isInitialized(artifact) ? artifact : "not initialized") +
                ", name='" + name + '\'' +
                ", format='" + format + '\'' +
                ", size=" + size +
                ", bitrate=" + bitrate +
                ", duration=" + duration +
                ", width=" + width +
                ", height=" + height +
                ", extra=" + extra +
                '}';
    }
}
