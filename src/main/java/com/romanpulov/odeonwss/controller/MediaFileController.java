package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.MediaFileEditDTO;
import com.romanpulov.odeonwss.dto.MediaFileTableDTO;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.service.MediaFileService;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/media-file", produces = MediaType.APPLICATION_JSON_VALUE)
public class MediaFileController {

    private final MediaFileService mediaFileService;

    public MediaFileController(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    @GetMapping("/table/{artifactId}")
    ResponseEntity<List<MediaFileTableDTO>> getTable(@PathVariable Long artifactId) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(mediaFileService.getTable(artifactId));
    }

    @GetMapping("/table-id-name/{artifactId}")
    ResponseEntity<List<IdNameDTO>> getTableIdName(@PathVariable Long artifactId) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(mediaFileService.getTableIdName(artifactId));
    }

    @GetMapping("/{id}")
    ResponseEntity<MediaFileEditDTO> get(@PathVariable Long id) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(mediaFileService.getById(id));
    }

    @PostMapping
    ResponseEntity<MediaFileEditDTO> post(@RequestBody MediaFileEditDTO dto) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(mediaFileService.insert(dto));
    }

    @PutMapping
    ResponseEntity<MediaFileEditDTO> put(@RequestBody MediaFileEditDTO dto)
            throws CommonEntityNotFoundException {
        return ResponseEntity.ok(mediaFileService.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws CommonEntityNotFoundException {
        mediaFileService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
