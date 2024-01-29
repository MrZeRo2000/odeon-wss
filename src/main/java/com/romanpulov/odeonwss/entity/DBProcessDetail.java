package com.romanpulov.odeonwss.entity;

import com.romanpulov.odeonwss.entity.converter.DateTimeConverter;
import com.romanpulov.odeonwss.entity.converter.ProcessingStatusConverter;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import org.hibernate.Hibernate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "process_details")
@AttributeOverride(name = "id", column = @Column(name = "prdt_id"))
public class DBProcessDetail extends AbstractBaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prif_id", referencedColumnName = "prif_id")
    @NotNull
    private DBProcessInfo dbProcessInfo;

    public @NotNull DBProcessInfo getDbProcessInfo() {
        return dbProcessInfo;
    }

    public void setDbProcessInfo(@NotNull DBProcessInfo dbProcessInfo) {
        this.dbProcessInfo = dbProcessInfo;
    }

    @Column(name = "prdt_message")
    @NotNull
    private String message;

    public @NotNull String getMessage() {
        return message;
    }

    public void setMessage(@NotNull String message) {
        this.message = message;
    }

    @Column(name = "prdt_status")
    @NotNull
    @Convert(converter = ProcessingStatusConverter.class)
    private ProcessingStatus processingStatus;

    public @NotNull ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(@NotNull ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    @Column(name = "prdt_rows")
    private Long rows;

    public Long getRows() {
        return rows;
    }

    public void setRows(Long rows) {
        this.rows = rows;
    }

    @Column(name = "prdt_upd_datm")
    @NotNull
    @Convert(converter = DateTimeConverter.class)
    private LocalDateTime updateDateTime;

    public @NotNull LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(@NotNull LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "dbProcessDetail", fetch = FetchType.LAZY)
    private List<DBProcessDetailAction> dbProcessDetailActions;

    public List<DBProcessDetailAction> getDbProcessDetailActions() {
        return dbProcessDetailActions;
    }

    public void setDbProcessDetailActions(List<DBProcessDetailAction> dbProcessDetailActions) {
        this.dbProcessDetailActions = dbProcessDetailActions;
    }

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "dbProcessDetail", fetch = FetchType.LAZY)
    public List<DBProcessDetailItem> dbProcessDetailItems;

    public List<DBProcessDetailItem> getDbProcessDetailItems() {
        return dbProcessDetailItems;
    }

    public void setDbProcessDetailItems(List<DBProcessDetailItem> dbProcessDetailItems) {
        this.dbProcessDetailItems = dbProcessDetailItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBProcessDetail dbProcessDetail = (DBProcessDetail) o;
        return getId().equals(dbProcessDetail.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "DBProcessDetail{" +
                "dbProcessInfo=" +  (Hibernate.isInitialized(dbProcessInfo) ? dbProcessInfo : dbProcessInfo.getId()) +
                ", message='" + message + '\'' +
                ", processingStatus=" + processingStatus +
                ", rows=" + rows +
                ", updateDate=" + updateDateTime +
                '}';
    }
}
