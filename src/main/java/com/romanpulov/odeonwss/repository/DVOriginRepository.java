package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.DVOriginDTO;
import com.romanpulov.odeonwss.entity.DVOrigin;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DVOriginRepository
        extends MappedMigratedIdJpaRepository<DVOrigin, Long>, EntityDTORepository<DVOrigin, DVOriginDTO> {
    @Query("SELECT COALESCE(MAX(o.id), 0) FROM DVOrigin o")
    Long getMaxId();

    @Query("SELECT dvo.id AS id, dvo.name AS name FROM DVOrigin dvo WHERE dvo.id = :id")
    Optional<DVOriginDTO> findDTOById(long id);

    @Query("SELECT dvo.id AS id, dvo.name AS name FROM DVOrigin dvo ORDER BY dvo.name")
    List<DVOriginDTO> findAllDTO();
}
