package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.repository.DVOriginRepository;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/dvorigin", produces = MediaType.APPLICATION_JSON_VALUE)
public class DVOriginController {
    private final DVOriginRepository dvOriginRepository;

    public DVOriginController(DVOriginRepository dvOriginRepository) {
        this.dvOriginRepository = dvOriginRepository;
    }

    @GetMapping("/table")
    ResponseEntity<List<IdNameDTO>> getDvOrigins() {
        return ResponseEntity.ok(dvOriginRepository.findAllByOrderByName());
    }
}
