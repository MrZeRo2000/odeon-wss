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
    Optional<DVProduct> findFirstByArtifactTypeAndOriginalTitle(ArtifactType artifactType, String originalTitle);

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


    @Query(value = "SELECT " +
            "dvp.dvpd_id AS id, " +
            "dvo.dvor_name AS dvOriginName, " +
            "dvp.dvpd_title AS title, " +
            "dvp.dvpd_orig_title AS originalTitle, " +
            "dvp.dvpd_year AS year, " +
            "dvp.dvpd_front_info AS frontInfo, " +
            "CASE WHEN dvp.dvpd_description IS NOT NULL THEN 1 END AS hasDescription, " +
            "CASE WHEN dvp.dvpd_notes IS NOT NULL THEN 1 END AS hasNotes, " +
            "dvc.dvct_name AS dvCategoryName, " +
            "CASE WHEN EXISTS(SELECT 1 FROM tracks_dv_products t WHERE t.dvpd_id = dvp.dvpd_id) THEN 1 END AS hasTracks " +
            "FROM main.dv_products dvp " +
            "LEFT JOIN main.dv_products_dv_categories dvp_dvc ON dvp.dvpd_id = dvp_dvc.dvpd_id " +
            "LEFT JOIN main.dv_categories dvc ON dvp_dvc.dvct_id = dvc.dvct_id " +
            "LEFT JOIN main.dv_origins dvo ON dvp.dvor_id = dvo.dvor_id " +
            "WHERE dvp.attp_id=:artifactTypeId " +
            "ORDER BY dvp.dvpd_title, dvc.dvct_name", nativeQuery = true)
    List<DVProductFlatDTO> findAllFlatDTOByArtifactTypeId(Long artifactTypeId);

    @Query("SELECT dvp.description as text FROM DVProduct dvp WHERE dvp.id=:id")
    Optional<TextDTO> findDescriptionById(Long id);

    @Query("SELECT dvp.notes as text FROM DVProduct dvp WHERE dvp.id=:id")
    Optional<TextDTO> findNotesById(Long id);
}
