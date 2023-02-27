package com.romanpulov.odeonwss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.odeonwss.dto.ProcessorRequestDTO;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ControllerProcessTest {

    final static Logger logger = LoggerFactory.getLogger(ControllerProcessTest.class);

    final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnErrorWrongParameter() throws Exception {

        String json = mapper.writeValueAsString(ProcessorRequestDTO.fromProcessorType("SSS"));

        this.mockMvc.perform(post("/api/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("Wrong parameter")));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void getResult() throws Exception {
        MvcResult mvcResult;

        this.mockMvc.perform(delete("/api/process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Matchers.is("Cleared")));

        mvcResult = this.mockMvc.perform(get("/api/process"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.is("Progress data not available")))
                .andReturn();
        logger.debug("Get result before execute: " + mvcResult.getResponse().getContentAsString());

        String json = mapper.writeValueAsString(ProcessorRequestDTO.fromProcessorType("MP3_VALIDATOR"));

        this.mockMvc.perform(post("/api/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Matchers.is("Started")));

        // to make sure it starts async
        this.mockMvc.perform(get("/api/process"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.is("Progress data not available")))
                .andReturn();

        Thread.sleep(1000L);

        mvcResult = this.mockMvc.perform(get("/api/process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processorType", Matchers.is("MP3_VALIDATOR")))
                .andExpect(jsonPath("$.processingStatus", Matchers.is("FAILURE")))
                .andExpect(jsonPath("$.processDetails").isArray())
                .andExpect(jsonPath("$.processDetails[0].info.message", Matchers.is("Started MP3 Validator")))
                .andReturn();
        logger.debug("Get result after execute: " + mvcResult.getResponse().getContentAsString());
    }
}
