package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVCategory;
import org.springframework.data.jpa.repository.Query;

public interface DVCategoryRepository extends MappedMigratedIdJpaRepository<DVCategory, Long> {
    @Query("SELECT COALESCE(MAX(c.id), 0) FROM DVCategory c")
    Long getMaxId();
}
