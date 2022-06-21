package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;

public interface EditableObjectService<O> {
    O getById(Long id) throws CommonEntityNotFoundException;
    O insert(O o) throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException;
    O update(O o) throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException;
    void deleteById(Long id) throws CommonEntityNotFoundException;
}
