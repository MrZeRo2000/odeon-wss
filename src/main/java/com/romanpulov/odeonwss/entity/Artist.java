package com.romanpulov.odeonwss.entity;

import com.romanpulov.odeonwss.entity.converter.ArtistTypeConverter;
import org.hibernate.HibernateException;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "artists")
@AttributeOverride(name = "id", column = @Column(name = "arts_id"))
@AttributeOverride(name = "insertDateTime", column = @Column(name = "arts_ins_datm"))
@AttributeOverride(name = "updateDateTime", column = @Column(name = "arts_upd_datm"))
@AttributeOverride(name = "migrationId", column = @Column(name = "arts_migration_id"))
public class Artist extends AbstractBaseMigratedEntity {
    @Column(name = "arts_type_code")
    @NotNull
    @Convert(converter = ArtistTypeConverter.class)
    private ArtistType type;

    public @NotNull ArtistType getType() {
        return type;
    }

    public void setType(@NotNull ArtistType type) {
        this.type = type;
    }

    @Column(name = "arts_name")
    @NotNull
    private String name;

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY)
    private List<Artifact> artifacts;

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<ArtistCategory> artistCategories;

    public List<ArtistCategory> getArtistCategories() {
        return artistCategories;
    }

    public void setArtistCategories(List<ArtistCategory> artistCategories) {
        this.artistCategories = artistCategories;
    }

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<ArtistDetail> artistDetails;

    public List<ArtistDetail> getArtistDetails() {
        return artistDetails;
    }

    public void setArtistDetails(List<ArtistDetail> artistDetails) {
        this.artistDetails = artistDetails;
    }

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<ArtistLyrics> artistLyrics;

    public List<ArtistLyrics> getArtistLyrics() {
        return artistLyrics;
    }

    public void setArtistLyrics(List<ArtistLyrics> artistLyrics) {
        this.artistLyrics = artistLyrics;
    }

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Track> tracks;

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    @PreRemove
    private void removeArtist() {
        if (artifacts != null && !artifacts.isEmpty()) {
            throw new HibernateException("Unable to delete " + this + " because it has child artifacts");
        }
        if (tracks != null && !tracks.isEmpty()) {
            throw new HibernateException("Unable to delete " + this + " because it has child tracks");
        }
    }

    public Artist() {
    }

    public Artist(Long id, @NotNull ArtistType type, @NotNull String name) {
        this.setId(id);
        this.type = type;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return getId().equals(artist.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + getId() +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
