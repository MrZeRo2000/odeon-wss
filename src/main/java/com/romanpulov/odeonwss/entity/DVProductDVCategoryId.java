package com.romanpulov.odeonwss.entity;

import java.io.Serializable;
import java.util.Objects;

public class DVProductDVCategoryId implements Serializable {
    private Long dvProductId;
    private Long dvCategoryId;

    public DVProductDVCategoryId() {
    }

    public DVProductDVCategoryId(Long dvProductId, Long dvCategoryId) {
        this.dvProductId = dvProductId;
        this.dvCategoryId = dvCategoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DVProductDVCategoryId that = (DVProductDVCategoryId) o;
        return Objects.equals(dvProductId, that.dvProductId) && Objects.equals(dvCategoryId, that.dvCategoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dvProductId, dvCategoryId);
    }
}
