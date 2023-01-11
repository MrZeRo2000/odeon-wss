package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVType;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface DVTypeRepository extends MappedIdJpaRepository<DVType, Long> {
    @Cacheable("dvTypes")
    List<DVType> getAllByOrderById();
}
