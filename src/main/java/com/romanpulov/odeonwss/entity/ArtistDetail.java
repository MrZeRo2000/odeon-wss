package com.romanpulov.odeonwss.entity;

import org.springframework.lang.Nullable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "artist_details")
@AttributeOverride(name = "id", column = @Column(name = "atdt_id"))
@AttributeOverride(name = "insertDateTime", column = @Column(name = "atdt_ins_datm"))
@AttributeOverride(name = "updateDateTime", column = @Column(name = "atdt_upd_datm"))
public class ArtistDetail extends AbstractBaseModifiableEntity {
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

    @Column(name = "atdt_biography")
    @NotNull
    private String biography;

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
