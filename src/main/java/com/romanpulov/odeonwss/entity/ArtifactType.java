package com.romanpulov.odeonwss.entity;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "artifact_types")
@AttributeOverride(name = "id", column = @Column(name = "attp_id"))
public class ArtifactType extends AbstractBaseEntity {
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
        this.setId(id);
        this.name = name;
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtifactType that = (ArtifactType) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "ArtifactType{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                '}';
    }

    public static ArtifactType withMP3() {
        ArtifactType artifactType = new ArtifactType();
        artifactType.setId(101L);
        artifactType.setName("MP3");

        return artifactType;
    }

    public static ArtifactType withLA() {
        ArtifactType artifactType = new ArtifactType();
        artifactType.setId(102L);
        artifactType.setName("LA");

        return artifactType;
    }

    public static ArtifactType withDVMusic() {
        ArtifactType artifactType = new ArtifactType();
        artifactType.setId(201L);
        artifactType.setName("Music");

        return artifactType;
    }

    public static ArtifactType withDVMovies() {
        ArtifactType artifactType = new ArtifactType();
        artifactType.setId(202L);
        artifactType.setName("Movies");
        artifactType.setParentId(200L);

        return artifactType;
   }

    public static ArtifactType withDVAnimation() {
        ArtifactType artifactType = new ArtifactType();
        artifactType.setId(203L);
        artifactType.setName("Animation");
        artifactType.setParentId(200L);

        return artifactType;
   }

    public static ArtifactType withDVDocumentary() {
        ArtifactType artifactType = new ArtifactType();
        artifactType.setId(204L);
        artifactType.setName("Documentary");
        artifactType.setParentId(200L);

        return artifactType;
    }

    public static ArtifactType withDVOther() {
        ArtifactType artifactType = new ArtifactType();
        artifactType.setId(205L);
        artifactType.setName("Documentary");
        artifactType.setParentId(200L);

        return artifactType;
    }
}
