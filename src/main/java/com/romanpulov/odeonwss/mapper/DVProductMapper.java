package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.DVProductDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.DVProduct;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DVProductMapper implements EntityDTOMapper<DVProduct, DVProductDTO> {
    private final DVOriginMapper dvOriginMapper;
    private final DVCategoryMapper dvCategoryMapper;

    public DVProductMapper(DVOriginMapper dvOriginMapper, DVCategoryMapper dvCategoryMapper) {
        this.dvOriginMapper = dvOriginMapper;
        this.dvCategoryMapper = dvCategoryMapper;
    }

    @Override
    public String getEntityName() {
        return "DVProduct";
    }

    @Override
    public DVProduct fromDTO(DVProductDTO dto) {
        DVProduct entity = new DVProduct();

        // immutable fields
        entity.setId(dto.getId());
        ArtifactType artifactType = new ArtifactType();
        artifactType.setId(dto.getId());
        entity.setArtifactType(artifactType);

        // mutable fields
        this.update(entity, dto);

        return entity;
    }

    @Override
    public void update(DVProduct entity, DVProductDTO dto) {
        entity.setDvOrigin(dvOriginMapper.fromDTO(dto.getDvOrigin()));

        entity.setTitle(dto.getTitle());
        entity.setOriginalTitle(dto.getOriginalTitle());
        entity.setYear(dto.getYear());
        entity.setFrontInfo(dto.getFrontInfo());
        entity.setDescription(dto.getDescription());
        entity.setNotes(dto.getNotes());

        entity.setDvCategories(dto.getDvCategories()
                .stream()
                .map(dvCategoryMapper::fromDTO)
                .collect(Collectors.toSet()));
    }
}
