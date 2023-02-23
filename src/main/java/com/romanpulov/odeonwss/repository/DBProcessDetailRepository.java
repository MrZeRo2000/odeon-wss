package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
import com.romanpulov.odeonwss.entity.DBProcessInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface DBProcessDetailRepository extends PagingAndSortingRepository<DBProcessDetail, Long> {
    List<DBProcessDetail> findAllByDbProcessInfoOrderByIdAsc(DBProcessInfo dbProcessInfo);
}
