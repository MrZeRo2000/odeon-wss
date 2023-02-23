package com.romanpulov.odeonwss.entity;

import com.romanpulov.odeonwss.entity.converter.DateConverter;
import com.romanpulov.odeonwss.entity.converter.ProcessingStatusConverter;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "process_details")
@AttributeOverride(name = "id", column = @Column(name = "prdt_id"))
public class DBProcessDetail extends AbstractBaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prif_id", referencedColumnName = "prif_id")
    @NotNull
    private DBProcessInfo dbProcessInfo;

    public DBProcessInfo getDbProcessInfo() {
        return dbProcessInfo;
    }

    public void setDbProcessInfo(DBProcessInfo dbProcessInfo) {
        this.dbProcessInfo = dbProcessInfo;
    }

    @Column(name = "prdt_message")
    @NotNull
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(name = "prdt_status")
    @NotNull
    @Convert(converter = ProcessingStatusConverter.class)
    private ProcessingStatus processingStatus;

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
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

    @Column(name = "prdt_upd_date")
    @NotNull
    @Convert(converter = DateConverter.class)
    private LocalDate updateDate;

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
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
                ", updateDate=" + updateDate +
                '}';
    }
}
