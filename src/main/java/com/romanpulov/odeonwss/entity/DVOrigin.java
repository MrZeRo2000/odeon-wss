package com.romanpulov.odeonwss.entity;

import org.hibernate.Hibernate;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DVOrigin dvOrigin = (DVOrigin) o;
        return getId() != null && Objects.equals(getId(), dvOrigin.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + getId() + ", " +
                "migrationId = " + getMigrationId() + ", " +
                "name = " + getName() + ")";
    }
}
