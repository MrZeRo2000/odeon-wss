package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.repository.TagRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/tag", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagController  {
    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        super();
        this.tagRepository = tagRepository;
    }

    @GetMapping("/table")
    ResponseEntity<List<IdNameDTO>> getTags() {
        return ResponseEntity.ok(tagRepository.findAllDTO());
    }
}
