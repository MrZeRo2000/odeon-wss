package com.romanpulov.odeonwss.entity;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(@Nullable Artist artist) {
        this.artist = artist;
    }

    @Column(name = "atlr_title")
    @NotNull
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "atlr_text")
    @NotNull
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
