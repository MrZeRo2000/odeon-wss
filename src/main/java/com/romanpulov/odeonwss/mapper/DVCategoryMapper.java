package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.DVCategoryDTO;
import com.romanpulov.odeonwss.entity.DVCategory;
import org.springframework.stereotype.Component;

@Component
public class DVCategoryMapper implements EntityDTOMapper<DVCategory, DVCategoryDTO> {
    @Override
    public String getEntityName() {
        return "DVCategory";
    }

    public DVCategory fromDTO(DVCategoryDTO dto) {
        DVCategory entity = new DVCategory();
        entity.setId(dto.getId());
        entity.setName(dto.getName());

        return entity;
    }

    public void update(DVCategory entity, DVCategoryDTO dto) {
        entity.setName(dto.getName());
    }
}
