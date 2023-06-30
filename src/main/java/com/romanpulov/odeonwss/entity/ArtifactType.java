package com.romanpulov.odeonwss.entity;

import org.springframework.lang.Nullable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "artifact_types")
@AttributeOverride(name = "id", column = @Column(name = "attp_id"))
@AttributeOverride(name = "insertDateTime", column = @Column(name = "attp_ins_datm"))
@AttributeOverride(name = "updateDateTime", column = @Column(name = "attp_upd_datm"))
public class ArtifactType extends AbstractBaseModifiableEntity {
    @Column(name = "attp_name")
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "attp_media_file_formats")
    private String mediaFileFormats;

    public String getMediaFileFormats() {
        return mediaFileFormats;
    }

    public void setMediaFileFormats(String mediaFileFormats) {
        this.mediaFileFormats = mediaFileFormats;
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
                ", mediaFileFormats='" + mediaFileFormats + '\'' +
                ", parentId=" + parentId +
                '}';
    }
}
