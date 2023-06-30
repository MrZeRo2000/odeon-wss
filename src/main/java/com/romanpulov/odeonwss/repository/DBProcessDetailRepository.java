package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
import com.romanpulov.odeonwss.entity.DBProcessInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface DBProcessDetailRepository extends CrudRepository<DBProcessDetail, Long> {
    List<DBProcessDetail> findAllByDbProcessInfoOrderByIdAsc(DBProcessInfo dbProcessInfo);
}
