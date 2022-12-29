package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Map;
import java.util.stream.Collectors;

public interface DVCategoryRepository extends JpaRepository<DVCategory, Long> {
    default Map<Long, DVCategory> findAllMap() {
        return findAll().stream().collect(Collectors.toMap(DVCategory::getId, v -> v));
    }

    @Query("SELECT COALESCE(MAX(c.id), 0) FROM DVCategory c")
    Long getMaxId();
}
