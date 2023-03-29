package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.entity.DVOrigin;
import org.springframework.stereotype.Component;

@Component
public class DVOriginMapper {
    public DVOrigin fromDTO(IdNameDTO dto) {
        DVOrigin dvOrigin = new DVOrigin();
        dvOrigin.setId(dto.getId());
        dvOrigin.setName(dto.getName());

        return dvOrigin;
    }

    public void update(DVOrigin dvOrigin, IdNameDTO dto) {
        dvOrigin.setName(dto.getName());
    }
}
