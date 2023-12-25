package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.user.DVProductUserImportDTO;
import com.romanpulov.odeonwss.dto.user.TrackUserImportDTO;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.EmptyParameterException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import com.romanpulov.odeonwss.service.user.DVProductUserImportService;
import com.romanpulov.odeonwss.service.user.ImportStats;
import com.romanpulov.odeonwss.service.user.TrackUserImportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/user-import", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserImportController {
    private final DVProductUserImportService dvProductUserImportService;
    private final TrackUserImportService trackUserImportService;

    public UserImportController(
            DVProductUserImportService dvProductUserImportService,
            TrackUserImportService trackUserImportService) {
        this.dvProductUserImportService = dvProductUserImportService;
        this.trackUserImportService = trackUserImportService;
    }

    @PostMapping("/dvproduct/analyze")
    ResponseEntity<ImportStats> dvProductAnalyze(@RequestBody DVProductUserImportDTO data)
            throws EmptyParameterException, CommonEntityNotFoundException {
        return ResponseEntity.ok(dvProductUserImportService.analyzeImportDVProducts(data));
    }

    @PostMapping("/dvproduct/execute")
    ResponseEntity<ImportStats> dvProductExecute(@RequestBody DVProductUserImportDTO data)
            throws EmptyParameterException, CommonEntityNotFoundException {
        return ResponseEntity.ok(dvProductUserImportService.executeImportDVProducts(data));
    }

    @PostMapping("/track/execute")
    ResponseEntity<ImportStats> trackExecute(@RequestBody TrackUserImportDTO data)
            throws EmptyParameterException, CommonEntityNotFoundException, WrongParameterValueException {
        return ResponseEntity.ok(trackUserImportService.executeImportTracks(data));
    }
}
