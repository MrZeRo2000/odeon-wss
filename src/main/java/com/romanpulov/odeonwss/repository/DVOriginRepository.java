package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DVOriginRepository
        extends MappedMigratedIdJpaRepository<DVOrigin, Long>, EntityDTORepository<DVOrigin, IdNameDTO> {
    @Query("SELECT COALESCE(MAX(o.id), 0) FROM DVOrigin o")
    Long getMaxId();

    @Query("SELECT dvo FROM DVOrigin dvo WHERE dvo.id = :id")
    Optional<IdNameDTO> findDTOById(long id);

    List<IdNameDTO> findAllByOrderByName();
}
