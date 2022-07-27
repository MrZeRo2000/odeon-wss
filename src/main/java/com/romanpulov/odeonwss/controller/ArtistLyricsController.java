package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtistLyricsEditDTO;
import com.romanpulov.odeonwss.dto.ArtistLyricsTableDTO;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.service.ArtistLyricsService;
import com.romanpulov.odeonwss.view.TextView;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/artist-lyrics", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistLyricsController {

    private final ArtistLyricsService artistLyricsService;

    public ArtistLyricsController(ArtistLyricsService artistLyricsService) {
        this.artistLyricsService = artistLyricsService;
    }

    @GetMapping("/table")
    ResponseEntity<List<ArtistLyricsTableDTO>> getTable() {
        return ResponseEntity.ok(artistLyricsService.getTable());
    }

    @GetMapping("/table/{id}")
    ResponseEntity<List<ArtistLyricsTableDTO>> getTable(@PathVariable Long id) {
        return ResponseEntity.ok(artistLyricsService.getTable(id));
    }

    @GetMapping("/text/{id}")
    ResponseEntity<TextView> getText(@PathVariable Long id) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(artistLyricsService.getText(id));
    }

    @GetMapping("/{id}")
    ResponseEntity<ArtistLyricsEditDTO> get(@PathVariable Long id) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(artistLyricsService.getById(id));
    }

    @PostMapping
    ResponseEntity<ArtistLyricsEditDTO> post(@RequestBody ArtistLyricsEditDTO dto)
            throws CommonEntityNotFoundException, CommonEntityAlreadyExistsException {
        return ResponseEntity.ok(artistLyricsService.insert(dto));
    }

    @PutMapping
    ResponseEntity<ArtistLyricsEditDTO> put(@RequestBody ArtistLyricsEditDTO dto)
            throws CommonEntityNotFoundException, CommonEntityAlreadyExistsException {
        return ResponseEntity.ok(artistLyricsService.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws CommonEntityNotFoundException {
        artistLyricsService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
