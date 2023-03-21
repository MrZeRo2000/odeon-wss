package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import com.romanpulov.odeonwss.view.IdTitleView;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/dvproduct", produces = MediaType.APPLICATION_JSON_VALUE)
public class DVProductController {
    private final ArtifactTypeRepository artifactTypeRepository;

    private final DVProductRepository dvProductRepository;

    public DVProductController(ArtifactTypeRepository artifactTypeRepository, DVProductRepository dvProductRepository) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.dvProductRepository = dvProductRepository;
    }

    @GetMapping("/dvproducts/table-id-title")
    ResponseEntity<List<IdTitleView>> getDvProducts(@RequestParam Long artifactTypeId)
            throws CommonEntityNotFoundException {
        ArtifactType artifactType = artifactTypeRepository.findById(artifactTypeId).orElseThrow(
                () -> new CommonEntityNotFoundException("DVProduct", artifactTypeId));
        return ResponseEntity.ok(dvProductRepository.findAllByArtifactTypeOrderByTitleAsc(artifactType));
    }
}
