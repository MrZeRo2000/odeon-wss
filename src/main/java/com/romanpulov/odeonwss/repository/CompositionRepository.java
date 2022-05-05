package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Composition;
import org.springframework.data.repository.CrudRepository;

public interface CompositionRepository extends CrudRepository<Composition, Long> {
    void deleteCompositionByArtifact(Artifact artifact);
}
