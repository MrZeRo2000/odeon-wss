package com.romanpulov.odeonwss.dto;

public class ArtistLyricsTableDTO {
    private Long id;

    private String artistName;

    private String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArtistLyricsTableDTO(Long id, String artistName, String title) {
        this.id = id;
        this.artistName = artistName;
        this.title = title;
    }

    public ArtistLyricsTableDTO() {
    }

    @Override
    public String toString() {
        return "ArtistLyricsTableDTO{" +
                "id=" + id +
                ", artistName='" + artistName + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
