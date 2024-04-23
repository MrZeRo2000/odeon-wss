package com.romanpulov.odeonwss.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArtifactDTOImpl implements ArtifactDTO {
    Long id;
    ArtifactTypeDTO artifactType;
    ArtistDTO artist;
    ArtistDTO performerArtist;
    String title;
    Long year;
    Long duration;
    Long size;
    List<String> tags = new ArrayList<>();
    LocalDateTime insertDateTime;

    public static ArtifactDTOImpl fromArtifactDTO(ArtifactDTO dto) {
        ArtifactDTOImpl instance = new ArtifactDTOImpl();
        instance.setId(dto.getId());
        instance.setArtifactType(dto.getArtifactType());
        instance.setArtist(dto.getArtist());
        instance.setPerformerArtist(dto.getPerformerArtist());
        instance.setTitle(dto.getTitle());
        instance.setYear(dto.getYear());
        instance.setDuration(dto.getDuration());
        instance.setSize(dto.getSize());
        instance.setTags(dto.getTags());
        instance.setInsertDateTime(dto.getInsertDateTime());

        return instance;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public ArtifactTypeDTO getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(ArtifactTypeDTO artifactType) {
        this.artifactType = artifactType;
    }

    @Override
    public ArtistDTO getArtist() {
        return artist;
    }

    public void setArtist(ArtistDTO artist) {
        this.artist = artist;
    }

    @Override
    public ArtistDTO getPerformerArtist() {
        return performerArtist;
    }

    public void setPerformerArtist(ArtistDTO performerArtist) {
        this.performerArtist = performerArtist;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    @Override
    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Override
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public LocalDateTime getInsertDateTime() {
        return insertDateTime;
    }

    public void setInsertDateTime(LocalDateTime insertDateTime) {
        this.insertDateTime = insertDateTime;
    }

    @Override
    public String toString() {
        return "ArtifactDTOImpl{" +
                "id=" + getId() +
                ", artifactType=" + getArtifactType() +
                ", artist=" + getArtist() +
                ", performerArtist=" + getPerformerArtist() +
                ", title='" + getTitle() + '\'' +
                ", year=" + getYear() +
                ", duration=" + getDuration() +
                ", size=" + getSize() +
                ", tags=" + getTags() +
                ", insertDateTime=" + getInsertDateTime() +
                '}';
    }
}
