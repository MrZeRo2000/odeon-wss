package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.AbstractBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;
import java.util.stream.Collectors;

public interface MappedIdJpaRepository<T extends AbstractBaseEntity, ID> extends JpaRepository<T, ID> {
    default Map<Long, T> findAllMap() {
        return findAll().stream().collect(Collectors.toMap(T::getId, v -> v));
    }
}
