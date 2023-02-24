package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessInfo;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DBProcessInfoRepository extends CrudRepository<DBProcessInfo, Long> {
    List<DBProcessInfo> findAllByUpdateDateTimeBetweenOrderByIdAsc(LocalDateTime startDate, LocalDateTime endDate);
}
