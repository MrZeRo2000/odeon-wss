package com.romanpulov.odeonwss.dto;

import java.util.Objects;

public class ArtistIdNameDTO {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArtistIdNameDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ArtistIdNameDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistIdNameDTO that = (ArtistIdNameDTO) o;
        return id.equals(that.id) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "ArtistIdNameDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
