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
        DVOrigin dvOrigin = new DVOrigin();
        dvOrigin.setId(dto.getId());
        dvOrigin.setName(dto.getName());

        return dvOrigin;
    }

    public void update(DVOrigin dvOrigin, DVOriginDTO dto) {
        dvOrigin.setName(dto.getName());
    }
}
