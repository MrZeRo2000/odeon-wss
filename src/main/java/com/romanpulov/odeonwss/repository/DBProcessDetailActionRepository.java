package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
import com.romanpulov.odeonwss.entity.DBProcessDetailAction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DBProcessDetailActionRepository extends CrudRepository<DBProcessDetailAction, Long> {
    List<DBProcessDetailAction> findByDbProcessDetailOrderByIdAsc(DBProcessDetail dbProcessDetail);
    Optional<DBProcessDetailAction> findFirstByDbProcessDetailOrderByIdAsc(DBProcessDetail dbProcessDetail);
}
