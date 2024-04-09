package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtifactTypeDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface ArtifactTypeRepository extends CrudRepository<ArtifactType, Long> {
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

    default boolean isVideo(long artifactTypeId) {
        return Optional.ofNullable(
                findById(artifactTypeId)
                        .orElseThrow()
                        .getParentId())
                .orElseThrow()
                .equals(200L);
    }

    @Query(value = """
        SELECT
          at.id AS id,
          at.name AS name,
          at.mediaFileFormats as mediaFileFormats
        FROM ArtifactType at
        WHERE at.id = :id
    """)
    Optional<ArtifactTypeDTO> findDTOById(long id);
}
