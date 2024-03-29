package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.BaseEntityDTO;
import com.romanpulov.odeonwss.entity.AbstractBaseEntity;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.EntityDTOMapper;
import com.romanpulov.odeonwss.repository.EntityDTORepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractEntityService<
        E extends AbstractBaseEntity,
        DTO extends BaseEntityDTO,
        R extends EntityDTORepository<E, DTO>> {

    @FunctionalInterface
    public interface OnProcessEntityHandler<E> {
        void onProcessEntity(E entity) throws CommonEntityNotFoundException;
    }

    private OnProcessEntityHandler<E> onBeforeSaveEntityHandler;

    @SuppressWarnings("unused")
    public OnProcessEntityHandler<E> getOnBeforeSaveEntityHandler()  {
        return onBeforeSaveEntityHandler;
    }

    @SuppressWarnings("unused")
    public void setOnBeforeSaveEntityHandler(OnProcessEntityHandler<E> onBeforeSaveEntityHandler) {
        this.onBeforeSaveEntityHandler = onBeforeSaveEntityHandler;
    }

    protected final R repository;

    protected final EntityDTOMapper<E, DTO> mapper;

    protected final String entityName;

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
    public DTO insert(DTO dto) throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        if (dto.getId() != null && repository.existsById(dto.getId())) {
            throw new CommonEntityAlreadyExistsException(entityName, dto.getId());
        } else {
            E entity = mapper.fromDTO(dto);
            if (onBeforeSaveEntityHandler != null) {
                onBeforeSaveEntityHandler.onProcessEntity(entity);
            }
            repository.save(entity);
            return getById(entity.getId());
        }
    }

    @Transactional
    public DTO update(DTO dto) throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        E entity = repository.findById(dto.getId())
                .orElseThrow(() -> new CommonEntityNotFoundException(this.entityName, dto.getId()));
        mapper.update(entity, dto);
        if (onBeforeSaveEntityHandler != null) {
            onBeforeSaveEntityHandler.onProcessEntity(entity);
        }
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
