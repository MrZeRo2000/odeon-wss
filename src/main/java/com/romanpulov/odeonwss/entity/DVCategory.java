package com.romanpulov.odeonwss.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "dv_categories")
@AttributeOverride(name = "id", column = @Column(name = "dvct_id"))
@AttributeOverride(name = "migrationId", column = @Column(name = "dvct_migration_id"))
public class DVCategory extends AbstractBaseMigratedEntity {
    @Column(name = "dvct_name")
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy = "dvCategories")
    private Set<DVProduct> dvProducts = new HashSet<>();

    public Set<DVProduct> getDvProducts() {
        return dvProducts;
    }

    public void setDvProducts(Set<DVProduct> dvProducts) {
        this.dvProducts = dvProducts;
    }

    public DVCategory() {
    }

    public DVCategory(Long id, String name) {
        this.setId(id);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DVCategory that = (DVCategory) o;
        return getId().equals(that.getId()) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), name);
    }

    @Override
    public String toString() {
        return "DVCategory{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                '}';
    }
}