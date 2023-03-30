package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.entity.AbstractBaseEntity;

public interface EntityDTOMapper<E extends AbstractBaseEntity, DTO> {
    String getEntityName();
    E fromDTO(DTO dto);
    void update(E entity, DTO dto);
}
