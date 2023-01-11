package com.romanpulov.odeonwss.entity;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractBaseMigratedEntity extends AbstractBaseEntity {
    private Long migrationId;

    public Long getMigrationId() {
        return migrationId;
    }

    public void setMigrationId(Long migrationId) {
        this.migrationId = migrationId;
    }
}
