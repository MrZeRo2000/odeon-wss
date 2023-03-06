package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.TrackEditDTO;
import com.romanpulov.odeonwss.dto.TrackTableDTO;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.service.TrackService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/track", produces = MediaType.APPLICATION_JSON_VALUE)
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping("/table/{artifactId}")
    ResponseEntity<List<TrackTableDTO>> getTable(@PathVariable Long artifactId) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(trackService.getTable(artifactId));
    }

    @GetMapping("/{id}")
    ResponseEntity<TrackEditDTO> get(@PathVariable Long id) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(trackService.getById(id));
    }

    @PostMapping
    ResponseEntity<TrackEditDTO> post(@RequestBody TrackEditDTO dto) throws CommonEntityNotFoundException  {
        return ResponseEntity.ok(trackService.insert(dto));
    }

    @PutMapping
    ResponseEntity<TrackEditDTO> put(@RequestBody TrackEditDTO dto)
            throws CommonEntityNotFoundException, CommonEntityAlreadyExistsException {
        return ResponseEntity.ok(trackService.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws CommonEntityNotFoundException {
        trackService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
