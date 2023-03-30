package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.AbstractEntityDTO;
import com.romanpulov.odeonwss.entity.AbstractBaseEntity;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.EntityDTOMapper;
import com.romanpulov.odeonwss.repository.EntityDTORepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractEntityService<
        E extends AbstractBaseEntity,
        DTO extends AbstractEntityDTO,
        R extends EntityDTORepository<E, DTO>> {

    protected final R repository;

    protected final EntityDTOMapper<E, DTO> mapper;

    private final String entityName;

    public AbstractEntityService(R repository, EntityDTOMapper<E, DTO> mapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityName = mapper.getEntityName();
    }

    @Transactional(readOnly = true)
    public DTO getById(Long id) throws CommonEntityNotFoundException {
        return repository
                .findDTOById(id)
                .orElseThrow(() -> new CommonEntityNotFoundException(this.entityName, id));
    }

    @Transactional
    public DTO insert(DTO dto) throws CommonEntityNotFoundException {
        E entity = mapper.fromDTO(dto);
        repository.save(entity);
        return getById(entity.getId());
    }

    @Transactional
    public DTO update(DTO dto) throws CommonEntityNotFoundException {
        E entity = repository.findById(dto.getId())
                .orElseThrow(() -> new CommonEntityNotFoundException(this.entityName, dto.getId()));
        mapper.update(entity, dto);
        repository.save(entity);
        return getById(entity.getId());
    }

    @Transactional
    public void deleteById(Long id) throws CommonEntityNotFoundException {
        E entity = repository.findById(id)
                .orElseThrow(() -> new CommonEntityNotFoundException(this.entityName, id));
        repository.delete(entity);
    }
}
