package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.BaseEntityDTO;
import com.romanpulov.odeonwss.entity.AbstractBaseEntity;

import java.lang.reflect.InvocationTargetException;

public class MapperUtils {
    public static <T extends AbstractBaseEntity> T createEntityFromDTO(BaseEntityDTO dto, Class<T> clazz) {
        if ((dto == null) || (dto.getId() == null)) {
            return null;
        } else {
            try {
                T instance = clazz.getDeclaredConstructor().newInstance();
                instance.setId(dto.getId());
                return instance;
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                     InvocationTargetException e) {
                return null;
            }
        }
    }
}
