package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.MediaFileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/media-file", produces = MediaType.APPLICATION_JSON_VALUE)
public class MediaFileController
        extends AbstractEntityServiceRestController<MediaFile, MediaFileDTO, MediaFileRepository, MediaFileService> {

    public MediaFileController(MediaFileService mediaFileService) {
        super(mediaFileService);
    }

    @GetMapping("/table/{artifactId}")
    ResponseEntity<List<MediaFileDTO>> getTable(@PathVariable Long artifactId) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.getTable(artifactId));
    }

    @GetMapping("/table-id-name-duration/{artifactId}")
    ResponseEntity<List<MediaFileDTO>> getTableIdNameDuration(@PathVariable Long artifactId)
            throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.getTableIdNameDuration(artifactId));
    }
}
