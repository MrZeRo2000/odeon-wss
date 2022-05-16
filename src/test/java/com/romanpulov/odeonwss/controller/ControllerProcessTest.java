package com.romanpulov.odeonwss.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.regex.MatchResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc

public class ControllerProcessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnErrorWrongParameter() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/process")
                        .param("processorRequest", "{processorType: \"SSS\"}")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Wrong parameter")));
    }

    @Test
    void shouldBeOk() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("processorRequest", "MP3_VALIDATOR")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Started")));
    }
}
