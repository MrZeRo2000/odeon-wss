package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryDetailDTO;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.ArtistCategoryMapper;
import com.romanpulov.odeonwss.repository.ArtistCategoryRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/artist-category-details", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistCategoryDetailsController {

    private final ArtistCategoryRepository artistCategoryRepository;
    private final ArtistCategoryMapper artistCategoryMapper;

    public ArtistCategoryDetailsController(ArtistCategoryRepository artistCategoryRepository, ArtistCategoryMapper artistCategoryMapper) {
        this.artistCategoryRepository = artistCategoryRepository;
        this.artistCategoryMapper = artistCategoryMapper;
    }

    @GetMapping("/{id}")
    ResponseEntity<ArtistCategoriesDetailDTO> get(@PathVariable Long id) throws CommonEntityNotFoundException {
        List<ArtistCategoryDetailDTO> acdList = artistCategoryRepository.getArtistCategoryDetailsByArtistId(id);

        if (acdList.size() == 0) {
            throw new CommonEntityNotFoundException("Artist Category Details", id);
        } else {
            return ResponseEntity.ok(
                    artistCategoryMapper.transformArtistCategoryDetailDTO(
                            acdList
                    )
            );
        }
    }
}
