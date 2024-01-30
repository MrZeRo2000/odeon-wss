package com.romanpulov.odeonwss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.odeonwss.dto.ProcessorRequestDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Order(1)
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
        // TODO Review and revise, sometimes it fails
        /*
        this.mockMvc.perform(get("/api/process"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.is("Progress data not available")))
                .andReturn();

         */

        Thread.sleep(1000L);

        mvcResult = this.mockMvc.perform(get("/api/process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processorType", Matchers.is("MP3_VALIDATOR")))
                .andExpect(jsonPath("$.processingStatus", Matchers.is("FAILURE")))
                .andExpect(jsonPath("$.processDetails").isArray())
                .andExpect(jsonPath("$.processDetails[0].message", Matchers.is("Started MP3 Validator")))
                .andReturn();
        logger.debug("Get result after execute: " + mvcResult.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void testTable() throws Exception {
        String json = mapper.writeValueAsString(ProcessorRequestDTO.fromProcessorType("LA_VALIDATOR"));

        this.mockMvc.perform(post("/api/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Matchers.is("Started")));

        Thread.sleep(1000L);

        var mvcResult = this.mockMvc.perform(get("/api/process/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(1))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(4)))
                .andExpect(jsonPath("$[0].processorType", Matchers.is("LA_VALIDATOR")))
                .andExpect(jsonPath("$[0].processingStatus", Matchers.is("FAILURE")))
                .andExpect(jsonPath("$[1].processorType", Matchers.is("MP3_VALIDATOR")))
                .andExpect(jsonPath("$[1].processingStatus", Matchers.is("FAILURE")))
                .andReturn();
        logger.debug("Get result after execute table: " + mvcResult.getResponse().getContentAsString());
    }

    @Test
    @Order(3)
    void testGetById() throws Exception {
        this.mockMvc.perform(get("/api/process/777"))
                .andExpect(status().isNotFound());

        var mvcResult = this.mockMvc.perform(get("/api/process/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.processorType", Matchers.is("MP3_VALIDATOR")))
                .andExpect(jsonPath("$.processingStatus", Matchers.is("FAILURE")))
                .andExpect(jsonPath("$.updateDateTime").exists())
                .andExpect(jsonPath("$.processDetails").isArray())

                .andExpect(jsonPath("$.processDetails[0].message", Matchers.is("Started MP3 Validator")))
                .andExpect(jsonPath("$.processDetails[0].status", Matchers.is("INFO")))
                .andExpect(jsonPath("$.processDetails[0].items").isArray())
                .andExpect(jsonPath("$.processDetails[0].items[0]").doesNotExist())

                .andExpect(jsonPath("$.processDetails[1].items").isArray())
                .andExpect(jsonPath("$.processDetails[1].items[0]", Matchers.is("Aerosmith")))
                .andExpect(jsonPath("$.processDetails[1].items[1]", Matchers.is("Kosheen")))
                .andExpect(jsonPath("$.processDetails[1].items[2]", Matchers.is("Various Artists")))
                .andExpect(jsonPath("$.processDetails[1].items[3]").doesNotExist())

                .andReturn();

        logger.debug("Get result after execute getById: " + mvcResult.getResponse().getContentAsString());
    }
}
