package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.BackupDatabaseInfoDTO;
import com.romanpulov.odeonwss.dto.MessageDTO;
import com.romanpulov.odeonwss.service.BackupService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/backup", produces = MediaType.APPLICATION_JSON_VALUE)
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @PostMapping
    ResponseEntity<MessageDTO> createBackup() {
        MessageDTO result = backupService.createBackup();
        if (result.getMessage() == null) {
            return ResponseEntity.internalServerError().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping
    ResponseEntity<BackupDatabaseInfoDTO> getBackupDatabaseInfo() {
        return ResponseEntity.ok(backupService.getBackupDatabaseInfo());
    }
}
