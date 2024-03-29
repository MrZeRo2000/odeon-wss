package com.romanpulov.odeonwss.entity;

import com.romanpulov.odeonwss.entity.converter.ArtistCategoryTypeConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "artist_categories")
@AttributeOverride(name = "id", column = @Column(name = "atct_id"))
@AttributeOverride(name = "insertDateTime", column = @Column(name = "atct_ins_datm"))
@AttributeOverride(name = "updateDateTime", column = @Column(name = "atct_upd_datm"))
@AttributeOverride(name = "migrationId", column = @Column(name = "atct_migration_id"))
public class ArtistCategory extends AbstractBaseMigratedEntity {
    @ManyToOne
    @JoinColumn(name = "arts_id", referencedColumnName = "arts_id")
    @NotNull
    private Artist artist;

    public @NotNull Artist getArtist() {
        return artist;
    }

    public void setArtist(@NotNull Artist artist) {
        this.artist = artist;
    }

    @Column(name = "atct_type_code")
    @NotNull
    @Convert(converter = ArtistCategoryTypeConverter.class)
    private ArtistCategoryType type;

    public @NotNull ArtistCategoryType getType() {
        return type;
    }

    public void setType(@NotNull ArtistCategoryType type) {
        this.type = type;
    }

    @Column(name = "atct_name")
    @NotNull
    private String name;

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }
}
