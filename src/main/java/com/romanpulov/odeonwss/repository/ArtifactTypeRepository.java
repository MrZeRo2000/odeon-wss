package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.ArtifactType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ArtifactTypeRepository extends CrudRepository<ArtifactType, Long> {
    @Cacheable("artifactTypesIdsInList")
    List<ArtifactType> getAllByIdIsIn(List<Long> ids);

    @Cacheable(value = "artifactTypeMP3", key = "'default'")
    default ArtifactType getWithMP3() {
        return findById(101L).orElseThrow();
    }

    @Cacheable(value = "artifactTypeLA", key = "'default'")
    default ArtifactType getWithLA() {
        return findById(102L).orElseThrow();
    }

    @Cacheable(value = "artifactTypeDVMusic", key = "'default'")
    default ArtifactType getWithDVMusic() {
        return findById(201L).orElseThrow();
    }

    @Cacheable(value = "artifactTypeDVMovies", key = "'default'")
    default ArtifactType getWithDVMovies() {
        return findById(202L).orElseThrow();
    }

    @Cacheable(value = "artifactTypeDVDocumentary", key = "'default'")
    default ArtifactType getWithDVDocumentary() {
        return findById(204L).orElseThrow();
    }

    @Cacheable(value = "artifactTypeDVAnimation", key = "'default'")
    default ArtifactType getWithDVAnimation() {
        return findById(203L).orElseThrow();
    }

    @Cacheable(value = "artifactTypeDVOther", key = "'default'")
    default ArtifactType getWithDVOther() {
        return findById(205L).orElseThrow();
    }
}
