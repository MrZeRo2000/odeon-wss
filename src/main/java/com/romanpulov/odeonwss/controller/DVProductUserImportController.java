package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.DVProductUserImportDTO;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.EmptyParameterException;
import com.romanpulov.odeonwss.service.user.DVProductUserImportService;
import com.romanpulov.odeonwss.service.user.ImportStats;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/dvproduct-user-import", produces = MediaType.APPLICATION_JSON_VALUE)
public class DVProductUserImportController {
    private final DVProductUserImportService dvProductUserImportService;

    public DVProductUserImportController(DVProductUserImportService dvProductUserImportService) {
        this.dvProductUserImportService = dvProductUserImportService;
    }

    @PostMapping("/analyze")
    ResponseEntity<ImportStats> analyze(@RequestBody DVProductUserImportDTO data)
            throws EmptyParameterException, CommonEntityNotFoundException {
        return ResponseEntity.ok(dvProductUserImportService.analyzeImportDVProducts(data));
    }

    @PostMapping("/execute")
    ResponseEntity<ImportStats> execute(@RequestBody DVProductUserImportDTO data)
            throws EmptyParameterException, CommonEntityNotFoundException {
        return ResponseEntity.ok(dvProductUserImportService.executeImportDVProducts(data));
    }
}
