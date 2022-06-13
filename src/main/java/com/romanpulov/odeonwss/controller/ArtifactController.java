package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtifactTableDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/artifact", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtifactController {

    private final ArtifactRepository artifactRepository;

    private final ArtifactTypeRepository artifactTypeRepository;

    public ArtifactController(ArtifactRepository artifactRepository, ArtifactTypeRepository artifactTypeRepository) {
        this.artifactRepository = artifactRepository;
        this.artifactTypeRepository = artifactTypeRepository;
    }

    @GetMapping("/table")
    ResponseEntity<List<ArtifactTableDTO>> getTable(@RequestParam String artistTypeCode, @RequestParam List<String> artifactTypeNames) {
        ArtistType artistType = ArtistType.fromCode(artistTypeCode);
        List<ArtifactType> artifactTypes = artifactTypeRepository.getAllByNameIsIn(artifactTypeNames);

        return ResponseEntity.ok(artifactRepository.getArtifactTableByArtistTypeAndArtifactTypes(artistType, artifactTypes));
    }
}
