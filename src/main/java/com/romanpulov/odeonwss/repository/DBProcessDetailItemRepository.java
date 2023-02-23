package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
import com.romanpulov.odeonwss.entity.DBProcessDetailItem;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface DBProcessDetailItemRepository extends PagingAndSortingRepository<DBProcessDetailItem, Long> {
    List<DBProcessDetailItem> findAllByDbProcessDetailOrderByIdAsc(DBProcessDetail dbProcessDetail);
}
