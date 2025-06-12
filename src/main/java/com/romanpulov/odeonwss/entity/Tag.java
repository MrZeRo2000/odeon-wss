package com.romanpulov.odeonwss.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "tags")
@AttributeOverride(name = "id", column = @Column(name = "ttag_id"))
@AttributeOverride(name = "insertDateTime", column = @Column(name = "ttag_ins_datm"))
@AttributeOverride(name = "updateDateTime", column = @Column(name = "ttag_upd_datm"))
public class Tag extends AbstractBaseModifiableEntity {
    @Column(name = "ttag_name")
    @NotNull
    private String name;

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tag tag)) return false;
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
