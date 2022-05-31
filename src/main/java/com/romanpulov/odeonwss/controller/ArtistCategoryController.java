package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtistCategoryArtistListDTO;
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
    private final ArtistCategoryMapper artistCategoryMapper;

    public ArtistCategoryController(ArtistCategoryRepository artistCategoryRepository, ArtistCategoryMapper artistCategoryMapper) {
        this.artistCategoryRepository = artistCategoryRepository;
        this.artistCategoryMapper = artistCategoryMapper;
    }

    @GetMapping("/all-with-artists")
    ResponseEntity<List<ArtistCategoryArtistListDTO>> getAllWithArtists() {
        return ResponseEntity.ok(
                artistCategoryMapper.transformArtistCategoryArtistsDTO(
                        artistCategoryRepository.getAllWithArtistOrdered()
                )
        );
    }
}
