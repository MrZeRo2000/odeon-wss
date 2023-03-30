package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.AbstractBaseEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface EntityDTORepository<E extends AbstractBaseEntity, DTO> extends CrudRepository<E, Long> {
    Optional<DTO> findDTOById(Long id);
}
