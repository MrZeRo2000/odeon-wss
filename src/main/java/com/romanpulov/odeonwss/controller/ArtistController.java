package com.romanpulov.odeonwss.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/artist", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistController {
}
