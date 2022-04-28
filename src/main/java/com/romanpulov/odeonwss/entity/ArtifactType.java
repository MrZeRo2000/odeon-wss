package com.romanpulov.odeonwss.entity;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "artifact_types")
public class ArtifactType {
    @Id
    @Column(name = "attp_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "attp_name")
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "attp_parent_id")
    @Nullable
    private Long parentId;

    @Nullable
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(@Nullable Long parentId) {
        this.parentId = parentId;
    }

    public ArtifactType() {
    }

    public ArtifactType(Long id, String name, @Nullable Long parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtifactType that = (ArtifactType) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ArtifactType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                '}';
    }
}
