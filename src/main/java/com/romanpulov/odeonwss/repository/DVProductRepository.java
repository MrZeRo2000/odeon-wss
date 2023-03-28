package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.DVProduct;
import com.romanpulov.odeonwss.view.IdTitleView;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DVProductRepository extends MappedMigratedIdJpaRepository<DVProduct, Long> {
    Optional<DVProduct> findFirstByArtifactTypeAndTitle(ArtifactType artifactType, String title);

    List<IdTitleView> findAllByArtifactTypeOrderByTitleAsc(ArtifactType artifactType);
}
