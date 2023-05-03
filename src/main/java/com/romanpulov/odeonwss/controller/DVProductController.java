package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.DVProductDTO;
import com.romanpulov.odeonwss.dto.IdTitleDTO;
import com.romanpulov.odeonwss.dto.TextDTO;
import com.romanpulov.odeonwss.entity.DVProduct;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import com.romanpulov.odeonwss.service.DVProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/dvproduct", produces = MediaType.APPLICATION_JSON_VALUE)
public class DVProductController
        extends AbstractEntityServiceRestController<DVProduct, DVProductDTO, DVProductRepository, DVProductService> {

    public DVProductController(DVProductService dvProductService) {
        super(dvProductService);
    }

    @GetMapping("/dvproducts/table-id-title")
    ResponseEntity<List<IdTitleDTO>> getTableIdTitle(@RequestParam Long artifactTypeId)
            throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.getTableIdTitle(artifactTypeId));
    }

    @GetMapping("/dvproducts/table")
    ResponseEntity<List<DVProductDTO>> getTable(@RequestParam Long artifactTypeId)
            throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.getTable(artifactTypeId));
    }

    @GetMapping("/description/{id}")
    ResponseEntity<TextDTO> getDescription(@PathVariable Long id)
            throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.getDescription(id));
    }

    @GetMapping("/notes/{id}")
    ResponseEntity<TextDTO> getNotes(@PathVariable Long id)
            throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.getNotes(id));
    }
}
