package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.DVCategoryDTO;
import com.romanpulov.odeonwss.entity.DVCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public interface DVCategoryRepository
        extends MappedMigratedIdJpaRepository<DVCategory, Long>, EntityDTORepository<DVCategory, DVCategoryDTO> {
    @Query("SELECT COALESCE(MAX(c.id), 0) FROM DVCategory c")
    Long getMaxId();

    @Query("SELECT dvc.id AS id, dvc.name AS name FROM DVCategory dvc WHERE dvc.id = :id")
    Optional<DVCategoryDTO> findDTOById(long id);

    @Query("SELECT dvc.id AS id, dvc.name AS name FROM DVCategory dvc ORDER BY dvc.name")
    List<DVCategoryDTO> findAllDTO();

    default Map<String, DVCategory> findAllNameMap() {
        return this.findAll().stream().collect(Collectors.toMap(DVCategory::getName, v -> v));
    }

}
