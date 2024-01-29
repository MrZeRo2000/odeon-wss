package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.process.ProcessInfoDTO;
import com.romanpulov.odeonwss.dto.process.ProcessInfoFlatDTO;
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
    SELECT
        d.id AS id,
        d.processorType AS processorType,
        d.processingStatus AS processingStatus,
        d.updateDateTime AS updateDateTime,
        pd.id AS detailId,
        pd.updateDateTime AS detailUpdateDateTime,
        pd.processingStatus AS detailProcessingStatus,
        pd.message AS detailMessage,
        pd.rows AS detailRows,
        di.value AS detailItem,
        da.actionType AS processingActionType,
        da.value AS processingActionValue
    FROM DBProcessInfo AS d
    LEFT OUTER JOIN DBProcessDetail AS pd ON pd.dbProcessInfo = d
    LEFT OUTER JOIN DBProcessDetailAction AS da ON da.dbProcessDetail = pd
    LEFT OUTER JOIN DBProcessDetailItem AS di ON di.dbProcessDetail = pd
    WHERE d.id = :id
    ORDER BY pd.id, di.value, da.value
    """)
    List<ProcessInfoFlatDTO> findFlatDTOByIdWithDetails(long id);
}
