package com.romanpulov.odeonwss.entity;

import com.romanpulov.odeonwss.entity.converter.ArtistCategoryTypeConverter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "artist_categories")
public class ArtistCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atct_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "arts_id", referencedColumnName = "arts_id")
    @NotNull
    private Artist artist;

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(@Nullable Artist artist) {
        this.artist = artist;
    }

    @Column(name = "atct_type_code")
    @NotNull
    @Convert(converter = ArtistCategoryTypeConverter.class)
    private ArtistCategoryType type;

    public ArtistCategoryType getType() {
        return type;
    }

    public void setType(ArtistCategoryType type) {
        this.type = type;
    }

    @Column(name = "atct_name")
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "atct_migration_id")
    @Nullable
    private Long migrationId;

    @Nullable
    public Long getMigrationId() {
        return migrationId;
    }

    public void setMigrationId(@Nullable Long migrationId) {
        this.migrationId = migrationId;
    }

}
