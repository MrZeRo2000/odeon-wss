package com.romanpulov.odeonwss.dto;

public class ArtistLyricsEditDTO {
    private Long id;

    private Long artistId;

    private String title;

    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArtistLyricsEditDTO(Long id, Long artistId, String title, String text) {
        this.id = id;
        this.artistId = artistId;
        this.title = title;
        this.text = text;
    }

    public ArtistLyricsEditDTO() {
    }

    @Override
    public String toString() {
        return "ArtistLyricsEditDTO{" +
                "id=" + id +
                ", artistId=" + artistId +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
