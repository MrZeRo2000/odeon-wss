package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface DVTypeRepository extends MappedIdJpaRepository<DVType, Long> {
    @Cacheable("dvTypes")
    List<DVType> getAllByOrderById();

    default Map<Long, DVType> findAllMap() {
        return findAll().stream().collect(Collectors.toMap(DVType::getId, v -> v));
    }
}
