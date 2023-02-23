package com.romanpulov.odeonwss.entity;

import com.romanpulov.odeonwss.entity.converter.DateConverter;
import com.romanpulov.odeonwss.entity.converter.ProcessingStatusConverter;
import com.romanpulov.odeonwss.entity.converter.ProcessorTypeConverter;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "process_info")
@AttributeOverride(name = "id", column = @Column(name = "prif_id"))
public class DBProcessInfo extends AbstractBaseEntity {

    @Column(name = "prif_type")
    @NotNull
    @Convert(converter = ProcessorTypeConverter.class)
    private ProcessorType processorType;

    public ProcessorType getProcessorType() {
        return processorType;
    }

    public void setProcessorType(ProcessorType processorType) {
        this.processorType = processorType;
    }

    @Column(name = "prif_status")
    @NotNull
    @Convert(converter = ProcessingStatusConverter.class)
    private ProcessingStatus processingStatus;

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    @Column(name = "prif_upd_date")
    @NotNull
    @Convert(converter = DateConverter.class)
    private LocalDate updateDate;

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "dbProcessInfo", fetch = FetchType.LAZY)
    private List<DBProcessDetail> dbProcessDetails;

    public List<DBProcessDetail> getDbProcessDetails() {
        return dbProcessDetails;
    }

    public void setDbProcessDetails(List<DBProcessDetail> dbProcessDetails) {
        this.dbProcessDetails = dbProcessDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBProcessInfo dbProcessInfo = (DBProcessInfo) o;
        return getId().equals(dbProcessInfo.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "DBProcessInfo{" +
                "processorType=" + processorType +
                ", processingStatus=" + processingStatus +
                ", insertDate=" + updateDate +
                '}';
    }
}