package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtifactTagDTO;
import com.romanpulov.odeonwss.entity.ArtifactTag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ArtifactTagRepository extends CrudRepository<ArtifactTag, Long> {
    @Query(value = """
        SELECT
            atg.artifact.id AS id,
            atg.name AS name
        FROM ArtifactTag atg
        WHERE atg.artifact.id = :artifactId
        ORDER BY atg.name
    """)
    List<ArtifactTagDTO> findAllFlatDTOByArtifactId(Long artifactId);
}
