package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.DVOriginMapper;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DVOriginService  implements EditableObjectService<IdNameDTO> {
    private final DVOriginRepository dvOriginRepository;

    private final DVOriginMapper dvOriginMapper;

    public DVOriginService(
            DVOriginRepository dvOriginRepository,
            DVOriginMapper dvOriginMapper) {
        this.dvOriginRepository = dvOriginRepository;
        this.dvOriginMapper = dvOriginMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public IdNameDTO getById(Long id) throws CommonEntityNotFoundException {
        return dvOriginRepository
                .findDVOriginById(id)
                .orElseThrow(() -> new CommonEntityNotFoundException("DVOrigin", id));
    }

    @Override
    @Transactional
    public IdNameDTO insert(IdNameDTO o) throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        DVOrigin dvOrigin = dvOriginMapper.fromDTO(o);
        dvOriginRepository.save(dvOrigin);
        return getById(dvOrigin.getId());
    }

    @Override
    @Transactional
    public IdNameDTO update(IdNameDTO o) throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        DVOrigin dvOrigin = dvOriginRepository.findById(o.getId())
                .orElseThrow(() -> new CommonEntityNotFoundException("DVOrigin", o.getId()));
        dvOriginMapper.update(dvOrigin, o);
        dvOriginRepository.save(dvOrigin);
        return getById(dvOrigin.getId());
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws CommonEntityNotFoundException {
        DVOrigin dvOrigin = dvOriginRepository.findById(id)
                .orElseThrow(() -> new CommonEntityNotFoundException("DVOrigin", id));
        dvOriginRepository.delete(dvOrigin);
    }
}
