package com.romanpulov.odeonwss.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        logger.info("Hello from logger");
        return "Hello from " + getClass().getSimpleName();
    }
}
