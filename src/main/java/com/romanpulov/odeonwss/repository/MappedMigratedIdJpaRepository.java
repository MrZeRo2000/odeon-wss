package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.AbstractBaseMigratedEntity;

import java.util.Map;
import java.util.stream.Collectors;

public interface MappedMigratedIdJpaRepository<T extends AbstractBaseMigratedEntity, ID> extends MappedIdJpaRepository<T, ID> {
    default Map<Long, T> findAllMigrationIdMap() {
        return findAll().stream().filter(v -> v.getMigrationId() != null).collect(Collectors.toMap(T::getMigrationId, v -> v));
    }
}
