package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.TrackDTO;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.TrackService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/track", produces = MediaType.APPLICATION_JSON_VALUE)
public class TrackController
        extends AbstractEntityServiceRestController<Track, TrackDTO, TrackRepository, TrackService> {

    public TrackController(TrackService trackService) {
        super(trackService);
    }

    @GetMapping("/table/{artifactId}")
    ResponseEntity<List<TrackDTO>> getTable(@PathVariable Long artifactId) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.getTable(artifactId));
    }

    @GetMapping("/table")
    ResponseEntity<List<TrackDTO>> getTableByProductId(@RequestParam Long dvProductId) {
        return ResponseEntity.ok(service.getTableByProductId(dvProductId));
    }

    @PostMapping("/reset-track-numbers/{artifactId}")
    public ResponseEntity<?> post(@PathVariable Long artifactId) throws CommonEntityNotFoundException {
        service.resetTrackNumbers(artifactId);
        return ResponseEntity.ok().build();
    }
}
