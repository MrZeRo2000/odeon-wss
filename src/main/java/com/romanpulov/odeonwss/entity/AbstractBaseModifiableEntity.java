package com.romanpulov.odeonwss.entity;

import com.romanpulov.odeonwss.entity.converter.DateTimeConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractBaseModifiableEntity extends AbstractBaseEntity {
    @Convert(converter = DateTimeConverter.class)
    private LocalDateTime insertDateTime;

    public LocalDateTime getInsertDateTime() {
        return insertDateTime;
    }

    public void setInsertDateTime(LocalDateTime insertDateTime) {
        this.insertDateTime = insertDateTime;
    }

    @Convert(converter = DateTimeConverter.class)
    private LocalDateTime updateDateTime;

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    @PrePersist
    void beforePersist() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (this.insertDateTime == null) {
            this.insertDateTime = currentDateTime;
        }
        if (this.updateDateTime == null) {
            this.updateDateTime = currentDateTime;
        }
    }

    @PreUpdate
    void beforeUpdate() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if ((this.updateDateTime == null) || currentDateTime.isAfter(this.updateDateTime)) {
            this.updateDateTime = currentDateTime;
        }
    }
}
