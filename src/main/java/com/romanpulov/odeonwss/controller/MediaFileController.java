package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.dto.RowsAffectedDTO;
import com.romanpulov.odeonwss.dto.TextDTO;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
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

    @GetMapping("/table-files/{artifactId}")
    ResponseEntity<List<TextDTO>> getTableFiles(@PathVariable Long artifactId) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.getMediaFiles(artifactId));
    }

    @GetMapping("/file-attributes")
    ResponseEntity<MediaFileDTO> getMediaFileAttributes(
            @RequestParam Long artifactId, @RequestParam String mediaFileName)
            throws CommonEntityNotFoundException, WrongParameterValueException {
        return ResponseEntity.ok(service.getMediaFileAttributes(artifactId, mediaFileName));
    }

    @PostMapping("/insert-media-files/{artifactId}")
    ResponseEntity<RowsAffectedDTO> postMediaFiles(
            @PathVariable Long artifactId,
            @RequestBody List<String> mediaFileNames)
            throws CommonEntityNotFoundException, WrongParameterValueException {
        return ResponseEntity.ok(service.insertMediaFiles(artifactId, mediaFileNames));
    }
}
