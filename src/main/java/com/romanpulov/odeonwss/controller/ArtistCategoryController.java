package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtistCategoryTableDTO;
import com.romanpulov.odeonwss.mapper.ArtistCategoryMapper;
import com.romanpulov.odeonwss.repository.ArtistCategoryRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/artist-category", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistCategoryController {

    private final ArtistCategoryRepository artistCategoryRepository;

    public ArtistCategoryController(ArtistCategoryRepository artistCategoryRepository) {
        this.artistCategoryRepository = artistCategoryRepository;
    }

    @GetMapping("/all-with-artists")
    ResponseEntity<List<ArtistCategoryTableDTO>> getAllWithArtists() {
        return ResponseEntity.ok(
                ArtistCategoryMapper.fromArtistCategoryArtistsDTO(
                        artistCategoryRepository.getAllWithArtistOrdered()
                )
        );
    }
}
