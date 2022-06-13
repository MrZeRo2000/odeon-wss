package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.ArtifactType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtifactTypeRepository extends CrudRepository<ArtifactType, Long> {
    @Cacheable("artifactType")
    List<ArtifactType> getAllByName(String name);

    @Cacheable("artifactTypes")
    List<ArtifactType> getAllByIdIsIn(List<Long> ids);
}
