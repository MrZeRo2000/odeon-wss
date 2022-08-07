package com.romanpulov.odeonwss.entity;

import com.romanpulov.odeonwss.entity.converter.ArtistTypeConverter;
import org.hibernate.HibernateException;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "arts_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "arts_type_code")
    @NotNull
    @Convert(converter = ArtistTypeConverter.class)
    private ArtistType type;

    public ArtistType getType() {
        return type;
    }

    public void setType(ArtistType type) {
        this.type = type;
    }

    @Column(name = "arts_name")
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "arts_migration_id")
    @Nullable
    private Long migrationId;

    @Nullable
    public Long getMigrationId() {
        return migrationId;
    }

    public void setMigrationId(@Nullable Long migrationId) {
        this.migrationId = migrationId;
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
    private List<Composition> compositions;

    public List<Composition> getCompositions() {
        return compositions;
    }

    public void setCompositions(List<Composition> compositions) {
        this.compositions = compositions;
    }

    @PreRemove
    private void removeArtist() {
        if (artifacts != null && artifacts.size() > 0) {
            throw new HibernateException("Unable to delete " + this + " because it has child artifacts");
        }
        if (compositions != null && compositions.size() > 0) {
            throw new HibernateException("Unable to delete " + this + " because it has child compositions");
        }
    }

    public Artist() {
    }

    public Artist(Long id, ArtistType type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return id.equals(artist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
