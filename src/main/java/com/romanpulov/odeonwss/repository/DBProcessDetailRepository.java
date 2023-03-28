package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
import com.romanpulov.odeonwss.entity.DBProcessInfo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface DBProcessDetailRepository extends PagingAndSortingRepository<DBProcessDetail, Long> {
    List<DBProcessDetail> findAllByDbProcessInfoOrderByIdAsc(DBProcessInfo dbProcessInfo);
}
