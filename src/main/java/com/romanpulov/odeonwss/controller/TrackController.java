package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.RowsAffectedDTO;
import com.romanpulov.odeonwss.dto.TrackDTO;
import com.romanpulov.odeonwss.dto.user.*;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.TrackService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/table-by-optional")
    ResponseEntity<List<TrackDTO>> getTableByOptional(
            @RequestParam(required = false) List<Long> artifactTypeIds,
            @RequestParam(required = false) List<Long> artistIds) {
        return ResponseEntity.ok(service.getTableByOptional(artifactTypeIds, artistIds));
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

    @PostMapping("/update-track-video-types")
    public ResponseEntity<RowsAffectedDTO> updateTrackVideoTypes(@RequestBody TrackDVTypeUserUpdateDTO dto)
        throws CommonEntityNotFoundException, WrongParameterValueException {
        if (dto.getArtifact() == null || dto.getArtifact().getId() == null) {
            throw new WrongParameterValueException("Artifact", "Value not found");
        }

        if (dto.getDvType() == null || dto.getDvType().getId() == null) {
            throw new WrongParameterValueException("DVType", "Value not found");

        }
        return ResponseEntity.ok(service.updateVideoTypes(
                dto.getArtifact().getId(),
                dto.getDvType().getId()));
    }

    @PostMapping("/update-selected-track-video-types")
    public ResponseEntity<RowsAffectedDTO> updateSelectedTrackVideoTypes(@RequestBody TrackSelectedDVTypeUserUpdateDTO dto)
        throws CommonEntityNotFoundException, WrongParameterValueException {
        if (dto.getArtifact() == null || dto.getArtifact().getId() == null) {
            throw new WrongParameterValueException("Artifact", "Value not found");
        }

        if (dto.getTrackIds() == null || dto.getTrackIds().isEmpty()) {
            throw new WrongParameterValueException("TrackIds", "Value not found");
        }

        if (dto.getDvType() == null || dto.getDvType().getId() == null) {
            throw new WrongParameterValueException("DVType", "Value not found");

        }

        return ResponseEntity.ok(service.updateSelectedVideoTypes(
                dto.getArtifact().getId(),
                dto.getTrackIds(),
                dto.getDvType().getId()));
    }

    @PutMapping("/update-tags")
    ResponseEntity<TrackDTO> updateTags(@RequestBody TrackDTO dto) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(service.updateTags(dto));
    }

    @PostMapping("/update-selected-track-tags")
    public ResponseEntity<RowsAffectedDTO> updateSelectedTrackTags(@RequestBody TrackSelectedTagsUserUpdateDTO dto)
            throws CommonEntityNotFoundException, WrongParameterValueException {
        if (dto.getArtifact() == null || dto.getArtifact().getId() == null) {
            throw new WrongParameterValueException("Artifact", "Value not found");
        }

        if (dto.getTrackIds() == null || dto.getTrackIds().isEmpty()) {
            throw new WrongParameterValueException("TrackIds", "Value not found");
        }

        if (dto.getTags() == null) {
            throw new WrongParameterValueException("Tags", "Value not found");

        }

        return ResponseEntity.ok(service.updateSelectedTags(
                dto.getArtifact().getId(),
                dto.getTrackIds(),
                dto.getTags()));
    }
}
