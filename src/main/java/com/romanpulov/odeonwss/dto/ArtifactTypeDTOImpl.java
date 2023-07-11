package com.romanpulov.odeonwss.dto;

public class ArtifactTypeDTOImpl implements ArtifactTypeDTO {
    Long id;
    String name;

    @Override
    public Long getId() {
        return id;
    }

    @Override
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
    public String toString() {
        return "ArtifactTypeDTOImpl{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
