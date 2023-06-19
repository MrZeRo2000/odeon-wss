package com.romanpulov.odeonwss.service.user;

import com.romanpulov.odeonwss.dto.DVCategoryDTO;
import com.romanpulov.odeonwss.dto.DVProductUserImportDTO;
import com.romanpulov.odeonwss.dto.DVProductUserImportDetailDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.DVCategory;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.entity.DVProduct;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.EmptyParameterException;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.DVCategoryRepository;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DVProductUserImportService {

    private final ArtifactTypeRepository artifactTypeRepository;

    private final DVOriginRepository dvOriginRepository;

    private final DVCategoryRepository dvCategoryRepository;

    private final DVProductRepository dvProductRepository;

    public DVProductUserImportService(
            ArtifactTypeRepository artifactTypeRepository,
            DVOriginRepository dvOriginRepository,
            DVCategoryRepository dvCategoryRepository,
            DVProductRepository dvProductRepository) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.dvOriginRepository = dvOriginRepository;
        this.dvCategoryRepository = dvCategoryRepository;
        this.dvProductRepository = dvProductRepository;
    }

    private ArtifactType getArtifactType(Long artifactTypeId)
            throws CommonEntityNotFoundException, EmptyParameterException {
        if (artifactTypeId == null) {
            throw new EmptyParameterException("artifactTypeId");
        }

        return artifactTypeRepository.findById(artifactTypeId).orElseThrow(
                () -> new CommonEntityNotFoundException("ArtifactType", artifactTypeId));
    }

    private DVOrigin getDvOrigin(Long dvOriginId)
            throws CommonEntityNotFoundException, EmptyParameterException {
        if (dvOriginId == null) {
            throw new EmptyParameterException("dvOriginId");
        }

        return dvOriginRepository.findById(dvOriginId).orElseThrow(
                () -> new CommonEntityNotFoundException("DVOrigin", dvOriginId)
        );
    }

    private Set<DVCategory> getDvCategories(Collection<DVCategoryDTO> data)
            throws CommonEntityNotFoundException, EmptyParameterException {
        Set<DVCategory> result = new HashSet<>();

        if (data != null) {
            Map<String, DVCategory> nameCategoryMap = dvCategoryRepository.findAllNameMap();

            for (DVCategoryDTO dto : data) {
                if (dto.getName() == null) {
                    throw new EmptyParameterException("category");
                }

                DVCategory dvCategory = nameCategoryMap.get(dto.getName());
                if (dvCategory == null) {
                    throw new CommonEntityNotFoundException("Category", dto.getName());
                }

                result.add(dvCategory);
            }
        }

        return result;
    }

    public ImportStats analyzeImportDVProducts(DVProductUserImportDTO data)
            throws CommonEntityNotFoundException, EmptyParameterException {
        ImportStats result = ImportStats.empty();

        ArtifactType artifactType = getArtifactType(data.getArtifactTypeId());
        // dvOrigin check
        getDvOrigin(data.getDvOriginId());
        // dvCategories check
        getDvCategories(data.getDvCategories());

        for (DVProductUserImportDetailDTO det: data.getDVProductDetails()) {
            if (det.getTitle() == null) {
                throw new EmptyParameterException("Title");
            }
            dvProductRepository.findFirstByArtifactTypeAndTitle(artifactType, det.getTitle()).ifPresentOrElse(
                    p -> result.addRowUpdated(det.getTitle()),
                    () -> result.addRowInserted(det.getTitle())
            );
        }

        return result;
    }

    public ImportStats executeImportDVProducts(DVProductUserImportDTO data)
            throws CommonEntityNotFoundException, EmptyParameterException {
        ImportStats result = ImportStats.empty();

        ArtifactType artifactType = getArtifactType(data.getArtifactTypeId());
        DVOrigin dvOrigin = getDvOrigin(data.getDvOriginId());
        Set<DVCategory> dvCategories = getDvCategories(data.getDvCategories());

        for (DVProductUserImportDetailDTO det: data.getDVProductDetails()) {
            if (det.getTitle() == null) {
                throw new EmptyParameterException("Title");
            }

            DVProduct dvProduct = dvProductRepository
                    .findFirstByArtifactTypeAndTitle(artifactType, det.getTitle())
                    .orElseGet(() -> {
                        DVProduct newDvProduct = new DVProduct();
                        newDvProduct.setArtifactType(artifactType);
                        newDvProduct.setTitle(det.getTitle());

                       return newDvProduct;
                    });
            dvProduct.setDvOrigin(dvOrigin);
            dvProduct.setOriginalTitle(det.getOriginalTitle());
            dvProduct.setYear(det.getYear());
            dvProduct.setDvCategories(dvCategories);

            if (dvProduct.getId() == null) {
                result.addRowInserted(det.getTitle());
            } else {
                result.addRowUpdated(det.getTitle());
            }

            dvProductRepository.save(dvProduct);
        }

        return result;
    }
}
