package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.mapper.DVOriginMapper;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
import org.springframework.stereotype.Service;

@Service
public class DVOriginService
        extends AbstractEntityService<DVOrigin, IdNameDTO, DVOriginRepository>
        implements EditableObjectService<IdNameDTO> {

    public DVOriginService(
            DVOriginRepository dvOriginRepository,
            DVOriginMapper dvOriginMapper) {
        super(dvOriginRepository, dvOriginMapper);
    }
}
