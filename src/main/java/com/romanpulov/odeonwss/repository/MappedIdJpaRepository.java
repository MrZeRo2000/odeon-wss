package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Map;

@NoRepositoryBean
public interface MappedIdJpaRepository<T, ID> extends JpaRepository<T, ID> {
}
