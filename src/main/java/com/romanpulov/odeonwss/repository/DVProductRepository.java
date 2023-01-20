package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVProduct;

import java.util.Optional;

public interface DVProductRepository extends MappedMigratedIdJpaRepository<DVProduct, Long> {
    Optional<DVProduct> getFirstByTitle(String title);
}
