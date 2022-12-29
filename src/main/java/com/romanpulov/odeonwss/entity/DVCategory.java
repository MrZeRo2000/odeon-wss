package com.romanpulov.odeonwss.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "dv_categories")
public class DVCategory {
    @Id
    @Column(name = "dvct_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DVCategory that = (DVCategory) o;
        return id.equals(that.id) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "DVCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}