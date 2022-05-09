package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Composition;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompositionRepository extends CrudRepository<Composition, Long> {
    void deleteCompositionByArtifact(Artifact artifact);

    List<Composition> getCompositionsByArtifact(Artifact artifact);
}
