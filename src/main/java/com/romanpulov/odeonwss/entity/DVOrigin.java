package com.romanpulov.odeonwss.entity;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "dv_origins")
@AttributeOverride(name = "id", column = @Column(name = "dvor_id"))
@AttributeOverride(name = "migrationId", column = @Column(name = "dvor_migration_id"))
public class DVOrigin extends AbstractBaseMigratedEntity {
    @Column(name = "dvor_name")
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DVOrigin() {
    }

    public DVOrigin(Long id, String name) {
        this.setId(id);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DVOrigin dvOrigin = (DVOrigin) o;
        return getId().equals(dvOrigin.getId()) && name.equals(dvOrigin.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "DVOrigin{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                '}';
    }
}
