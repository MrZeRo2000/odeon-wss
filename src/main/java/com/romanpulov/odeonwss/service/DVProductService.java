package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.DVProductDTO;
import com.romanpulov.odeonwss.dto.DVProductFlatDTO;
import com.romanpulov.odeonwss.dto.DVProductTransformer;
import com.romanpulov.odeonwss.dto.IdTitleDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.DVProduct;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.DVProductMapper;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DVProductService
        extends AbstractEntityService<DVProduct, DVProductDTO, DVProductRepository>
        implements EditableObjectService<DVProductDTO> {

    private final ArtifactTypeRepository artifactTypeRepository;
    private final DVProductRepository dvProductRepository;
    private final DVProductTransformer transformer;

    public DVProductService(
            ArtifactTypeRepository artifactTypeRepository,
            DVProductRepository repository,
            DVProductMapper mapper,
            DVProductTransformer transformer) {
        super(repository, mapper);
        this.artifactTypeRepository = artifactTypeRepository;
        this.dvProductRepository = repository;
        this.transformer = transformer;
    }

    @Override
    public DVProductDTO getById(Long id) throws CommonEntityNotFoundException {
        List<DVProductFlatDTO> dtoList = dvProductRepository.findFlatDTOById(id);
        if (dtoList.size() == 0) {
            throw new CommonEntityNotFoundException(this.entityName, id);
        } else {
            return transformer.transform(dtoList).get(0);
        }
    }

    public List<IdTitleDTO> getTableIdTitle(Long artifactTypeId) throws CommonEntityNotFoundException {
        ArtifactType artifactType = artifactTypeRepository.findById(artifactTypeId).orElseThrow(
                () -> new CommonEntityNotFoundException("ArtifactType", artifactTypeId));
        return dvProductRepository.findAllByArtifactTypeOrderByTitleAsc(artifactType);
    }

    public List<DVProductDTO> getTable(Long artifactTypeId) throws CommonEntityNotFoundException {
        if (artifactTypeRepository.existsById(artifactTypeId)) {
            return transformer.transform(dvProductRepository.findAllFlatDTOByArtifactTypeId(artifactTypeId));
        } else {
            throw new CommonEntityNotFoundException("ArtifactType", artifactTypeId);
        }
    }
}
