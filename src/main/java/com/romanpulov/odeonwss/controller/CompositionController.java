package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.dto.CompositionTableDTO;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.service.CompositionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/composition", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompositionController {

    private final CompositionService compositionService;

    public CompositionController(CompositionService compositionService) {
        this.compositionService = compositionService;
    }

    @GetMapping("/table/{artifactId}")
    ResponseEntity<List<CompositionTableDTO>> getTable(@PathVariable Long artifactId) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(compositionService.getTable(artifactId));
    }

    @GetMapping("/{id}")
    ResponseEntity<CompositionEditDTO> get(@PathVariable Long id) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(compositionService.getById(id));
    }

    @PostMapping
    ResponseEntity<CompositionEditDTO> post(@RequestBody CompositionEditDTO dto) throws CommonEntityNotFoundException  {
        return ResponseEntity.ok(compositionService.insert(dto));
    }

    @PutMapping
    ResponseEntity<CompositionEditDTO> put(@RequestBody CompositionEditDTO dto)
            throws CommonEntityNotFoundException, CommonEntityAlreadyExistsException {
        return ResponseEntity.ok(compositionService.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws CommonEntityNotFoundException {
        compositionService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
