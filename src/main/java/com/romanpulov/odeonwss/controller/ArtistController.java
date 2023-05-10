package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtistDTO;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.service.ArtistService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/artist", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistController {

    private final ArtistRepository artistRepository;

    private final ArtistService artistService;

    public ArtistController(
            ArtistRepository artistRepository,
            ArtistService artistService) {
        this.artistRepository = artistRepository;
        this.artistService = artistService;
    }

    @GetMapping("/artists/table-id-name")
    ResponseEntity<List<IdNameDTO>> getArtistList(@RequestParam String artistTypeCode) {
        return ResponseEntity.ok(artistRepository.getByTypeOrderByName(ArtistType.fromCode(artistTypeCode)));
    }

    @GetMapping("/artists/table")
    ResponseEntity<List<ArtistDTO>> getTable() {
        return ResponseEntity.ok(artistService.getTable());
    }
}
