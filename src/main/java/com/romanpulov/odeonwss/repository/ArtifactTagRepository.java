package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.ArtifactTag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ArtifactTagRepository extends CrudRepository<ArtifactTag, Long> {
    List<ArtifactTag> findByArtifactId(Long artifactId);
}
