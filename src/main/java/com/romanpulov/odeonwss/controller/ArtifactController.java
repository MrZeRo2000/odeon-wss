package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.service.ArtifactService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/artifact", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtifactController
        extends AbstractEntityServiceRestController<Artifact, ArtifactDTO, ArtifactRepository, ArtifactService> {

    public ArtifactController(ArtifactService artifactService) {
        super(artifactService);
    }

    @GetMapping("/table")
    ResponseEntity<List<ArtifactDTO>> getTable(@RequestParam String artistTypeCode, @RequestParam List<String> artifactTypeCodes) {
        ArtistType artistType = ArtistType.fromCode(artistTypeCode);
        List<Long> artifactTypeIds = artifactTypeCodes.stream().map(Long::valueOf).collect(Collectors.toList());
        return ResponseEntity.ok(service.getTable(artistType, artifactTypeIds));
    }
}
