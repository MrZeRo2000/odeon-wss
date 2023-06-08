package com.romanpulov.odeonwss.dto;

import java.util.Objects;

public class IdNameDTOImpl implements IdNameDTO {
    private Long id;
    private String name;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdNameDTOImpl idNameDTO = (IdNameDTOImpl) o;
        return Objects.equals(id, idNameDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "IdNameDTOImpl{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
