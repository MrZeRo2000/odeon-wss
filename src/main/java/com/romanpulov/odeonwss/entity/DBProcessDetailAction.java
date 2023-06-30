package com.romanpulov.odeonwss.entity;

import com.romanpulov.odeonwss.entity.converter.ProcessingActionTypeConverter;
import com.romanpulov.odeonwss.service.processor.model.ProcessingActionType;
import org.hibernate.Hibernate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "process_detail_actions")
@AttributeOverride(name = "id", column = @Column(name = "prda_id"))
public class DBProcessDetailAction extends AbstractBaseEntity {
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

    @Column(name = "prda_type")
    @Convert(converter = ProcessingActionTypeConverter.class)
    @NotNull
    private ProcessingActionType actionType;

    public ProcessingActionType getActionType() {
        return actionType;
    }

    public void setActionType(ProcessingActionType actionType) {
        this.actionType = actionType;
    }

    @Column(name = "prda_value")
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
        DBProcessDetailAction that = (DBProcessDetailAction) o;
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
                "actionType = " + getActionType() + ", " +
                "value = " + getValue() + ")";
    }
}
