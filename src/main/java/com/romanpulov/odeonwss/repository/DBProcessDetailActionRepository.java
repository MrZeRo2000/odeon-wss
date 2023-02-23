package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
import com.romanpulov.odeonwss.entity.DBProcessDetailAction;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface DBProcessDetailActionRepository extends PagingAndSortingRepository<DBProcessDetailAction, Long> {
    List<DBProcessDetailAction> findByDbProcessDetailOrderByIdAsc(DBProcessDetail dbProcessDetail);
}
