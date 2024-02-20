package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.RowsAffectedDTO;
import com.romanpulov.odeonwss.dto.TrackDTO;
import com.romanpulov.odeonwss.dto.user.ChapterDurationsTransformer;
import com.romanpulov.odeonwss.dto.user.TrackDurationsUserUpdateDTO;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
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
    private final ChapterDurationsTransformer chapterDurationsTransformer;

    public TrackController(
            TrackService trackService,
            ChapterDurationsTransformer chapterDurationsTransformer) {
        super(trackService);
        this.chapterDurationsTransformer = chapterDurationsTransformer;
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
    public ResponseEntity<RowsAffectedDTO> resetTrackNumbers(@PathVariable Long artifactId) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.resetTrackNumbers(artifactId));
    }

    @PostMapping("/update-track-durations")
    public ResponseEntity<RowsAffectedDTO> updateTrackDurations(@RequestBody TrackDurationsUserUpdateDTO dto)
        throws CommonEntityNotFoundException, WrongParameterValueException {
        if (dto.getArtifact() == null || dto.getArtifact().getId() == null) {
            throw new WrongParameterValueException("Artifact", "Value not found");
        }

        if (dto.getMediaFile() == null || dto.getMediaFile().getId() == null) {
            throw new WrongParameterValueException("MediaFile", "Value not found");
        }

        if (dto.getChapters() == null || dto.getChapters().isEmpty()) {
            throw new WrongParameterValueException("Chapters", "Value not found");
        }

        return ResponseEntity.ok(service.updateDurationsFromMediaFile(
                dto.getArtifact().getId(),
                dto.getMediaFile().getId(),
                chapterDurationsTransformer.transform(dto.getChapters())));
    }
}
