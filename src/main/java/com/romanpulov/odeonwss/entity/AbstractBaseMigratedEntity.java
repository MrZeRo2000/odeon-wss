package com.romanpulov.odeonwss.entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractBaseMigratedEntity extends AbstractBaseModifiableEntity {
    private Long migrationId;

    public Long getMigrationId() {
        return migrationId;
    }

    public void setMigrationId(Long migrationId) {
        this.migrationId = migrationId;
    }
}
