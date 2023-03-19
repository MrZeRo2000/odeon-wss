package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.DVProduct;
import com.romanpulov.odeonwss.view.IdTitleView;

import java.util.List;
import java.util.Optional;

public interface DVProductRepository extends MappedMigratedIdJpaRepository<DVProduct, Long> {
    Optional<DVProduct> findFirstByArtifactTypeAndTitle(ArtifactType artifactType, String title);

    List<IdTitleView> findAllByArtifactTypeOrderByTitleAsc(ArtifactType artifactType);
}
