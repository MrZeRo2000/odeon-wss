package com.romanpulov.odeonwss.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "artifact_tags")
@AttributeOverride(name = "id", column = @Column(name = "atft_id"))
@AttributeOverride(name = "insertDateTime", column = @Column(name = "atft_ins_datm"))
@AttributeOverride(name = "updateDateTime", column = @Column(name = "atft_upd_datm"))
public class ArtifactTag extends AbstractBaseModifiableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artf_id", referencedColumnName = "artf_id")
    @NotNull
    private Artifact artifact;

    public @NotNull Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(@NotNull Artifact artifact) {
        this.artifact = artifact;
    }

    @Column(name = "atft_name")
    @NotNull
    private String name;

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }
}
