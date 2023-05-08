package com.romanpulov.odeonwss.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "dv_types")
@AttributeOverride(name = "id", column = @Column(name = "dvtp_id"))
@AttributeOverride(name = "insertDateTime", column = @Column(name = "dvtp_ins_datm"))
@AttributeOverride(name = "updateDateTime", column = @Column(name = "dvtp_upd_datm"))
public class DVType extends AbstractBaseModifiableEntity {
    @Column(name = "dvtp_name")
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DVType() {
    }

    public DVType(Long id, String name) {
        this.setId(id);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DVType dvType = (DVType) o;
        return getId().equals(dvType.getId()) && name.equals(dvType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), name);
    }

    @Override
    public String toString() {
        return "DVType{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                '}';
    }
}
