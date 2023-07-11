package com.romanpulov.odeonwss.dto;

public class ArtistLyricsDTOImpl implements ArtistLyricsDTO {
    private Long id;
    private Long artistId;
    private String artistName;
    private String title;
    private String text;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    @Override
    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ArtistLyricsDTOImpl{" +
                "id=" + id +
                ", artistId=" + artistId +
                ", artistName='" + artistName + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
