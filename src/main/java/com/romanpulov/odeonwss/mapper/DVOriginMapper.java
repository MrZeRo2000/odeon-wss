package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.DVOriginDTO;
import com.romanpulov.odeonwss.entity.DVOrigin;
import org.springframework.stereotype.Component;

@Component
public class DVOriginMapper implements EntityDTOMapper<DVOrigin, DVOriginDTO> {
    @Override
    public String getEntityName() {
        return "DVOrigin";
    }

    public DVOrigin fromDTO(DVOriginDTO dto) {
        DVOrigin entity = new DVOrigin();
        entity.setId(dto.getId());
        entity.setName(dto.getName());

        return entity;
    }

    public void update(DVOrigin entity, DVOriginDTO dto) {
        entity.setName(dto.getName());
    }
}
