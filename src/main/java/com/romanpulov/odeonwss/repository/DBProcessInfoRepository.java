package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.DBProcessInfoDTO;
import com.romanpulov.odeonwss.entity.DBProcessInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface DBProcessInfoRepository extends CrudRepository<DBProcessInfo, Long> {
    List<DBProcessInfo> findAllByUpdateDateTimeBetweenOrderByIdAsc(LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
        SELECT
          d.id AS id,
          d.processorType AS processorType,
          d.processingStatus AS processingStatus,
          d.updateDateTime AS updateDateTime
        FROM DBProcessInfo AS d
        ORDER BY d.updateDateTime DESC
    """)
    List<DBProcessInfoDTO> findAllOrderedByUpdateDateTime();
}
