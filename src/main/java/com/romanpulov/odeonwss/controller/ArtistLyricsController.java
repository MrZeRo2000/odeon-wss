package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtistLyricsDTO;
import com.romanpulov.odeonwss.entity.ArtistLyrics;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.repository.ArtistLyricsRepository;
import com.romanpulov.odeonwss.service.ArtistLyricsService;
import com.romanpulov.odeonwss.dto.TextDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/artist-lyrics", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistLyricsController
        extends AbstractEntityServiceRestController<
        ArtistLyrics, ArtistLyricsDTO, ArtistLyricsRepository, ArtistLyricsService> {

    public ArtistLyricsController(ArtistLyricsService artistLyricsService) {
        super(artistLyricsService);
    }

    @GetMapping("/table")
    ResponseEntity<List<ArtistLyricsDTO>> getTable() {
        return ResponseEntity.ok(service.getTable());
    }

    @GetMapping("/table/{id}")
    ResponseEntity<List<ArtistLyricsDTO>> getTable(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTable(id));
    }

    @GetMapping("/text/{id}")
    ResponseEntity<TextDTO> getText(@PathVariable Long id) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.getText(id));
    }
}
