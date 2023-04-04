package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.AbstractEntityDTO;
import com.romanpulov.odeonwss.entity.AbstractBaseEntity;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.repository.EntityDTORepository;
import com.romanpulov.odeonwss.service.AbstractEntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public class AbstractEntityServiceRestController<
        E extends AbstractBaseEntity,
        DTO extends AbstractEntityDTO,
        R extends EntityDTORepository<E, DTO>,
        S extends AbstractEntityService<E, DTO, R>> {

    protected final S service;

    public AbstractEntityServiceRestController(S service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    ResponseEntity<DTO> get(@PathVariable Long id) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    ResponseEntity<DTO> post(@RequestBody DTO dto)
            throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        return ResponseEntity.ok(service.insert(dto));
    }

    @PutMapping
    ResponseEntity<DTO> put(@RequestBody DTO dto) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws CommonEntityNotFoundException {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
