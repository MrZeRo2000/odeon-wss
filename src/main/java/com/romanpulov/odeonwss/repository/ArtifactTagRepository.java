package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.ArtifactTag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ArtifactTagRepository extends CrudRepository<ArtifactTag, Long> {}
