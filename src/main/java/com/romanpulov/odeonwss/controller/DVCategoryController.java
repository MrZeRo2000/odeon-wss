package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.DVCategoryDTO;
import com.romanpulov.odeonwss.entity.DVCategory;
import com.romanpulov.odeonwss.repository.DVCategoryRepository;
import com.romanpulov.odeonwss.service.DVCategoryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/dvcategory", produces = MediaType.APPLICATION_JSON_VALUE)
public class DVCategoryController extends AbstractEntityServiceRestController<
        DVCategory,
        DVCategoryDTO,
        DVCategoryRepository,
        DVCategoryService> {
    private final DVCategoryRepository dvCategoryRepository;

    public DVCategoryController(DVCategoryRepository dvCategoryRepository, DVCategoryService dvCategoryService) {
        super(dvCategoryService);
        this.dvCategoryRepository = dvCategoryRepository;
    }

    @GetMapping("/table")
    ResponseEntity<List<DVCategoryDTO>> getDvCategories() {
        return ResponseEntity.ok(dvCategoryRepository.findAllDTO());
    }
}
