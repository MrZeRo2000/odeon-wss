package com.romanpulov.odeonwss.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "artist_lyrics")
@AttributeOverride(name = "id", column = @Column(name = "atlr_id"))
@AttributeOverride(name = "insertDateTime", column = @Column(name = "atlr_ins_datm"))
@AttributeOverride(name = "updateDateTime", column = @Column(name = "atlr_upd_datm"))
public class ArtistLyrics extends AbstractBaseModifiableEntity {
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

    @Column(name = "atlr_title")
    @NotNull
    private String title;

    public @NotNull String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    @Column(name = "atlr_text")
    @NotNull
    private String text;

    public @NotNull String getText() {
        return text;
    }

    public void setText(@NotNull String text) {
        this.text = text;
    }
}
