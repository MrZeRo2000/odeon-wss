package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.process.ProcessInfoDTO;
import com.romanpulov.odeonwss.entity.DBProcessInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    List<ProcessInfoDTO> findAllOrderedByUpdateDateTime();

    @Query("""
    SELECT d
    FROM DBProcessInfo as d
    LEFT JOIN FETCH d.dbProcessDetails
    WHERE d.id = :id
    """)
    Optional<DBProcessInfo> findByIdWithDetails(long id);
}
