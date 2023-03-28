package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.view.IdNameView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface DVOriginRepository extends MappedMigratedIdJpaRepository<DVOrigin, Long> {
    @Query("SELECT COALESCE(MAX(o.id), 0) FROM DVOrigin o")
    Long getMaxId();

    List<IdNameView> findAllByOrderByName();
}
