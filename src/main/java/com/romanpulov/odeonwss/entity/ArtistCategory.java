package com.romanpulov.odeonwss.entity;

import com.romanpulov.odeonwss.entity.converter.ArtistCategoryTypeConverter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "artist_categories")
@AttributeOverride(name = "id", column = @Column(name = "atct_id"))
@AttributeOverride(name = "migrationId", column = @Column(name = "atct_migration_id"))
public class ArtistCategory extends AbstractBaseMigratedEntity {
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
}
