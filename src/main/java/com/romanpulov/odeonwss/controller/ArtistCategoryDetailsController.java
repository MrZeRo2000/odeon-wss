package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.service.ArtistService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/artist-category-details", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistCategoryDetailsController {

    private final ArtistService artistService;

    public ArtistCategoryDetailsController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/{id}")
    ResponseEntity<ArtistCategoriesDetailDTO> get(@PathVariable Long id) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(artistService.getById(id));
    }

    @PostMapping
    ResponseEntity<ArtistCategoriesDetailDTO> post(@RequestBody ArtistCategoriesDetailDTO acd)
            throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException
    {
        return ResponseEntity.ok(artistService.insert(acd));
    }

    @PutMapping()
    ResponseEntity<ArtistCategoriesDetailDTO> put(@RequestBody ArtistCategoriesDetailDTO acd)
            throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException
    {
        return ResponseEntity.ok(artistService.update(acd));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws CommonEntityNotFoundException {
        artistService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}