package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
import com.romanpulov.odeonwss.entity.DBProcessDetailItem;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface DBProcessDetailItemRepository extends PagingAndSortingRepository<DBProcessDetailItem, Long> {
    List<DBProcessDetailItem> findAllByDbProcessDetailOrderByIdAsc(DBProcessDetail dbProcessDetail);
}
