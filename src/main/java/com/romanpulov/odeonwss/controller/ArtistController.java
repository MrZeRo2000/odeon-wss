package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtistIdNameDTO;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/artist", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistController {

    private final ArtistRepository artistRepository;

    public ArtistController(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @GetMapping
    ResponseEntity<List<ArtistIdNameDTO>> getArtistList() {
        return ResponseEntity.ok(artistRepository.getAllIdName());
    }
}
