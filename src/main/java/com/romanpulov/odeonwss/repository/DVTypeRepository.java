package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface DVTypeRepository extends MappedIdJpaRepository<DVType, Long> {
    @Cacheable("dvTypes")
    List<DVType> getAllByOrderById();
}
