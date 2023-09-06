package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.*;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.DVCategory;
import com.romanpulov.odeonwss.entity.DVProduct;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.DVProductMapper;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.DVCategoryRepository;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DVProductService
        extends AbstractEntityService<DVProduct, DVProductDTO, DVProductRepository>
        implements EditableObjectService<DVProductDTO> {

    private final ArtifactTypeRepository artifactTypeRepository;
    private final DVProductRepository dvProductRepository;
    private final DVProductTransformer transformer;

    public DVProductService(
            ArtifactTypeRepository artifactTypeRepository,
            DVOriginRepository dvOriginRepository,
            DVCategoryRepository dvCategoryRepository,
            DVProductRepository repository,
            DVProductMapper mapper,
            DVProductTransformer transformer) {
        super(repository, mapper);
        this.artifactTypeRepository = artifactTypeRepository;
        this.dvProductRepository = repository;
        this.transformer = transformer;

        this.setOnBeforeSaveEntityHandler(entity -> {
            Long artifactTypeId = entity.getArtifactType().getId();
            if (artifactTypeId != null && !artifactTypeRepository.existsById(artifactTypeId)) {
                throw new CommonEntityNotFoundException("ArtifactType", artifactTypeId);
            }

            Long dvOriginId = entity.getDvOrigin().getId();
            if (dvOriginId != null && !dvOriginRepository.existsById(dvOriginId)) {
                throw new CommonEntityNotFoundException("DVOrigin", dvOriginId);
            }

            for (DVCategory dvCategory: entity.getDvCategories()) {
                Long dvCategoryId = dvCategory.getId();
                if (dvCategoryId == null) {
                    throw new CommonEntityNotFoundException("DVCategory", 0L);
                } else if (!dvCategoryRepository.existsById(dvCategoryId)) {
                    throw new CommonEntityNotFoundException("DVCategory", dvCategoryId);
                }
            }
        });
    }

    @Override
    public DVProductDTO getById(Long id) throws CommonEntityNotFoundException {
        List<DVProductFlatDTO> dtoList = dvProductRepository.findFlatDTOById(id);
        if (dtoList.isEmpty()) {
            throw new CommonEntityNotFoundException(this.entityName, id);
        } else {
            return transformer.transform(dtoList, false).get(0);
        }
    }

    public List<IdTitleOriginalTitleDTO> getTableIdTitle(Long artifactTypeId) throws CommonEntityNotFoundException {
        ArtifactType artifactType = artifactTypeRepository.findById(artifactTypeId).orElseThrow(
                () -> new CommonEntityNotFoundException("ArtifactType", artifactTypeId));
        return dvProductRepository.findAllByArtifactTypeOrderByTitleAsc(artifactType);
    }

    public List<DVProductDTO> getTable(Long artifactTypeId) throws CommonEntityNotFoundException {
        if (artifactTypeRepository.existsById(artifactTypeId)) {
            return transformer.transform(dvProductRepository.findAllFlatDTOByArtifactTypeId(artifactTypeId), true);
        } else {
            throw new CommonEntityNotFoundException("ArtifactType", artifactTypeId);
        }
    }

    public TextDTO getDescription(Long id) throws CommonEntityNotFoundException {
        return dvProductRepository
                .findDescriptionById(id)
                .orElseThrow(() -> new CommonEntityNotFoundException("DVProduct", id));
    }

    public TextDTO getNotes(Long id) throws CommonEntityNotFoundException {
        return dvProductRepository
                .findNotesById(id)
                .orElseThrow(() -> new CommonEntityNotFoundException("DVProduct", id));
    }

    public Optional<DVProduct> findProductByArtifactTypeAndTitle(ArtifactType artifactType, String title) {
        return Optional.ofNullable(dvProductRepository.findFirstByArtifactTypeAndTitle(artifactType, title)
                .orElse(dvProductRepository.findFirstByArtifactTypeAndOriginalTitle(artifactType, title)
                        .orElse(null))
        );
    }
}
