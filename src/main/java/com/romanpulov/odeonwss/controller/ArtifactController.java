package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtifactEditDTO;
import com.romanpulov.odeonwss.dto.ArtifactTableDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.service.ArtifactService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/artifact", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtifactController {

    private final ArtifactRepository artifactRepository;

    private final ArtifactTypeRepository artifactTypeRepository;

    private final ArtifactService artifactService;


    public ArtifactController(ArtifactRepository artifactRepository, ArtifactTypeRepository artifactTypeRepository, ArtifactService artifactService) {
        this.artifactRepository = artifactRepository;
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactService = artifactService;
    }

    @GetMapping("/table")
    ResponseEntity<List<ArtifactTableDTO>> getTable(@RequestParam String artistTypeCode, @RequestParam List<String> artifactTypeCodes) {
        ArtistType artistType = ArtistType.fromCode(artistTypeCode);
        List<ArtifactType> artifactTypes = artifactTypeRepository.getAllByIdIsIn(artifactTypeCodes.stream().map(Long::valueOf).collect(Collectors.toList()));

        return ResponseEntity.ok(artifactRepository.getArtifactTableByArtistTypeAndArtifactTypes(artistType, artifactTypes));
    }

    @GetMapping("/{id}")
    ResponseEntity<ArtifactEditDTO> get(@PathVariable Long id) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(artifactService.getAEById(id));
    }

    @PostMapping
    ResponseEntity<ArtifactEditDTO> post(@RequestBody ArtifactEditDTO aed) throws CommonEntityNotFoundException  {
        return ResponseEntity.ok(artifactService.insertAED(aed));
    }

    @PutMapping
    ResponseEntity<ArtifactEditDTO> put(@RequestBody ArtifactEditDTO aed) throws CommonEntityNotFoundException  {
        return ResponseEntity.ok(artifactService.updateAED(aed));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws CommonEntityNotFoundException {
        artifactService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
