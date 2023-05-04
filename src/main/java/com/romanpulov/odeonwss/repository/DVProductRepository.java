package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.DVProductDTO;
import com.romanpulov.odeonwss.dto.DVProductFlatDTO;
import com.romanpulov.odeonwss.dto.TextDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.DVProduct;
import com.romanpulov.odeonwss.dto.IdTitleDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DVProductRepository
        extends MappedMigratedIdJpaRepository<DVProduct, Long>, EntityDTORepository<DVProduct, DVProductDTO> {
    Optional<DVProduct> findFirstByArtifactTypeAndTitle(ArtifactType artifactType, String title);

    List<IdTitleDTO> findAllByArtifactTypeOrderByTitleAsc(ArtifactType artifactType);

    @Query("SELECT " +
            "dvp.id AS id, " +
            "dvp.artifactType.id AS artifactTypeId, " +
            "dvo.id AS dvOriginId, " +
            "dvo.name AS dvOriginName, " +
            "dvp.title AS title, " +
            "dvp.originalTitle AS originalTitle, " +
            "dvp.year AS year, " +
            "dvp.frontInfo AS frontInfo, " +
            "dvp.description AS description, " +
            "dvp.notes AS notes, " +
            "dvc.id AS dvCategoryId, " +
            "dvc.name AS dvCategoryName " +
            "FROM DVProduct dvp " +
            "LEFT JOIN DVProductDVCategory dvp_dvc ON dvp.id = dvp_dvc.dvProductId " +
            "LEFT JOIN DVCategory dvc ON dvp_dvc.dvCategoryId = dvc.id " +
            "LEFT JOIN DVOrigin dvo ON dvp.dvOrigin = dvo " +
            "WHERE dvp.id=:id " +
            "ORDER BY dvc.name")
    List<DVProductFlatDTO> findFlatDTOById(Long id);


    @Query("SELECT " +
            "dvp.id AS id, " +
            "dvo.name AS dvOriginName, " +
            "dvp.title AS title, " +
            "dvp.originalTitle AS originalTitle, " +
            "dvp.year AS year, " +
            "dvp.frontInfo AS frontInfo, " +
            "CASE WHEN dvp.description IS NOT NULL THEN true ELSE false END AS hasDescription, " +
            "CASE WHEN dvp.notes IS NOT NULL THEN true ELSE false END AS hasNotes, " +
            "dvc.name AS dvCategoryName " +
            "FROM DVProduct dvp " +
            "LEFT JOIN DVProductDVCategory dvp_dvc ON dvp.id = dvp_dvc.dvProductId " +
            "LEFT JOIN DVCategory dvc ON dvp_dvc.dvCategoryId = dvc.id " +
            "LEFT JOIN DVOrigin dvo ON dvp.dvOrigin = dvo " +
            "WHERE dvp.artifactType.id=:artifactTypeId " +
            "ORDER BY dvp.title, dvc.name")
    List<DVProductFlatDTO> findAllFlatDTOByArtifactTypeId(Long artifactTypeId);

    @Query("SELECT dvp.description as text FROM DVProduct dvp WHERE dvp.id=:id")
    Optional<TextDTO> findDescriptionById(Long id);

    @Query("SELECT dvp.notes as text FROM DVProduct dvp WHERE dvp.id=:id")
    Optional<TextDTO> findNotesById(Long id);
}
