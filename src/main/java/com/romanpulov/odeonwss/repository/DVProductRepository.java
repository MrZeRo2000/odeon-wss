package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;
import java.util.stream.Collectors;

public interface DVProductRepository extends JpaRepository<DVProduct, Long> {
    default Map<Long, DVProduct> findAllMigrationIdMap() {
        return findAll().stream().filter(v -> v.getMigrationId() != null).collect(Collectors.toMap(DVProduct::getMigrationId, v -> v));
    }

}
