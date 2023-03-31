package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.DVCategoryDTO;
import com.romanpulov.odeonwss.entity.DVCategory;
import com.romanpulov.odeonwss.mapper.DVCategoryMapper;
import com.romanpulov.odeonwss.repository.DVCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class DVCategoryService
        extends AbstractEntityService<DVCategory, DVCategoryDTO, DVCategoryRepository>
        implements EditableObjectService<DVCategoryDTO> {

    public DVCategoryService(
            DVCategoryRepository dvCategoryRepository,
            DVCategoryMapper dvCategoryMapper) {
        super(dvCategoryRepository, dvCategoryMapper);
    }
}
