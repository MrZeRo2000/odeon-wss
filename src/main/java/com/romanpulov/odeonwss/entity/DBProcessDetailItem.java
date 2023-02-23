package com.romanpulov.odeonwss.entity;

import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "process_detail_items")
@AttributeOverride(name = "id", column = @Column(name = "prdi_id"))
public class DBProcessDetailItem extends AbstractBaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prdt_id", referencedColumnName = "prdt_id")
    @NotNull
    private DBProcessDetail dbProcessDetail;

    public DBProcessDetail getDbProcessDetail() {
        return dbProcessDetail;
    }

    public void setDbProcessDetail(DBProcessDetail dbProcessDetail) {
        this.dbProcessDetail = dbProcessDetail;
    }

    @Column(name = "prdi_value")
    @NotNull
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DBProcessDetailItem that = (DBProcessDetailItem) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + getId() + ", " +
                "value = " + getValue() + ")";
    }
}
