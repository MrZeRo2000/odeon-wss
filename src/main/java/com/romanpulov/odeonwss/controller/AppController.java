package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.dto.AppInfoDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppController {

    private final AppConfiguration appConfiguration;

    public AppController(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @GetMapping("app/info")
    ResponseEntity<AppInfoDTO> getVersion() {
        return ResponseEntity.ok(new AppInfoDTO(appConfiguration.getVersion()));
    }
}
