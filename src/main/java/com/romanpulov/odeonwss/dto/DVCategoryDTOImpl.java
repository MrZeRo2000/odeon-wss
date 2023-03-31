package com.romanpulov.odeonwss.dto;

import java.util.Objects;

public class DVCategoryDTOImpl implements DVCategoryDTO {
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

    public DVCategoryDTOImpl() {
    }

    public DVCategoryDTOImpl(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DVCategoryDTOImpl that = (DVCategoryDTOImpl) o;
        return Objects.equals(id, that.id) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "DVCategoryDTOImpl{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
