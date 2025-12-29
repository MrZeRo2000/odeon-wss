package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.service.BackupService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ControllerBackupTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerProcessTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BackupService backupService;

    @Test
    void testInfo() throws Exception {
        backupService.removeBackups();

        var result01 = mockMvc.perform(get("/api/backup").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$.backupFileCount", Matchers.equalTo(0)))
                .andExpect(jsonPath("$.lastBackupDateTime").doesNotExist())
                .andReturn();
        logger.debug("After removeBackups: " + result01.getResponse().getContentAsString());

        var result02 = mockMvc.perform(post("/api/backup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message", Matchers.equalTo("odeon-test-backup")))
                .andReturn();
        logger.debug("createBackup: " + result02.getResponse().getContentAsString());

        var result03 = mockMvc.perform(get("/api/backup").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(2)))
                .andExpect(jsonPath("$.backupFileCount", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.lastBackupDateTime").exists())
                .andReturn();
        logger.debug("After createBackup: " + result03.getResponse().getContentAsString());

        mockMvc.perform(post("/api/backup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message", Matchers.equalTo("odeon-test-backup")));

        var result04 = mockMvc.perform(get("/api/backup").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(2)))
                .andExpect(jsonPath("$.backupFileCount", Matchers.equalTo(2)))
                .andExpect(jsonPath("$.lastBackupDateTime").exists())
                .andReturn();
        logger.debug("After createBackup 2: " + result04.getResponse().getContentAsString());

        backupService.removeBackups();
    }
}
