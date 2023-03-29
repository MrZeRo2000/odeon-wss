package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.repository.ArtistDetailRepository;
import com.romanpulov.odeonwss.dto.BiographyDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/artist-detail", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistDetailController {

    private final ArtistDetailRepository artistDetailRepository;

    public ArtistDetailController(ArtistDetailRepository artistDetailRepository) {
        this.artistDetailRepository = artistDetailRepository;
    }

    @GetMapping("/{id}")
    ResponseEntity<BiographyDTO> get(@PathVariable Long id) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(
                artistDetailRepository.findArtistDetailById(id).orElseThrow(() ->
                        new CommonEntityNotFoundException("ArtistDetail", id))
        );
    }
}
