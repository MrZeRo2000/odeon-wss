package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtistDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.service.ArtistService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/artist", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistController
        extends AbstractEntityServiceRestController<Artist, ArtistDTO, ArtistRepository, ArtistService> {

    private final ArtistRepository artistRepository;

    public ArtistController(
            ArtistRepository artistRepository,
            ArtistService artistService) {
        super(artistService);
        this. artistRepository = artistRepository;
    }

    @GetMapping("/artists/table-id-name")
    ResponseEntity<List<IdNameDTO>> getArtistList(@RequestParam String artistTypeCode) {
        return ResponseEntity.ok(artistRepository.getByTypeOrderByName(ArtistType.fromCode(artistTypeCode)));
    }

    @GetMapping("/artists/table")
    ResponseEntity<List<ArtistDTO>> getTable() {
        return ResponseEntity.ok(service.getTable());
    }
}
