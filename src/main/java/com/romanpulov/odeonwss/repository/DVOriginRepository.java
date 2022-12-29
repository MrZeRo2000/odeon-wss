package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVOrigin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Map;
import java.util.stream.Collectors;

public interface DVOriginRepository extends JpaRepository<DVOrigin, Long> {
    default Map<Long, DVOrigin> findAllMap() {
        return findAll().stream().collect(Collectors.toMap(DVOrigin::getId, v -> v));
    }

    @Query("SELECT COALESCE(MAX(o.id), 0) FROM DVOrigin o")
    Long getMaxId();
}
