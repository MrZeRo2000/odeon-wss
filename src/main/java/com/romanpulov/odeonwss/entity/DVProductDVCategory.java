package com.romanpulov.odeonwss.entity;

import org.hibernate.Hibernate;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "dv_products_dv_categories")
@IdClass(DVProductDVCategoryId.class)
public class DVProductDVCategory {
    @Id
    @Column(name = "dvpd_id")
    private Long dvProductId;

    @Id
    @Column(name = "dvct_id")
    private Long dvCategoryId;

    public Long getDvProductId() {
        return dvProductId;
    }

    public Long getDvCategoryId() {
        return dvCategoryId;
    }

    public DVProductDVCategory() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DVProductDVCategory that = (DVProductDVCategory) o;
        return getDvProductId() != null && Objects.equals(getDvProductId(), that.getDvProductId())
                && getDvCategoryId() != null && Objects.equals(getDvCategoryId(), that.getDvCategoryId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dvProductId, dvCategoryId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "dvProductId = " + dvProductId + ", " +
                "dvCategoryId = " + dvCategoryId + ")";
    }
}
