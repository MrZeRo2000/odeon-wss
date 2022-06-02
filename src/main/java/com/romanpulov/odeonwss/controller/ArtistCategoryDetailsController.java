package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryDetailDTO;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.ArtistCategoryMapper;
import com.romanpulov.odeonwss.repository.ArtistCategoryRepository;
import com.romanpulov.odeonwss.service.ArtistService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/artist-category-details", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistCategoryDetailsController {

    private final ArtistService artistService;

    public ArtistCategoryDetailsController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/{id}")
    ResponseEntity<ArtistCategoriesDetailDTO> get(@PathVariable Long id) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(artistService.getACDById(id));
    }
}
