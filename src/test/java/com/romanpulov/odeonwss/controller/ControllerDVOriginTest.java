package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerDVOriginTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerDVOriginTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DVOriginRepository dvOriginRepository;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(1)
    @Rollback(value = false)
    void testGenerateTestData() {
        logger.debug("Generating data");

        dvOriginRepository.save(
                new EntityDVOriginBuilder()
                        .withId(1)
                        .withName("Second origin")
                        .build()
        );

        dvOriginRepository.save(
                new EntityDVOriginBuilder()
                        .withName("First origin")
                        .build()
        );

        assertThat(dvOriginRepository.findAllByOrderByName().size()).isEqualTo(2);
    }

    @Test
    @Order(2)
    void testValidateAllIdName() throws Exception {
        var result = mockMvc.perform(get("/api/dvorigin/dvorigins/table-id-name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo("First origin")))
                .andExpect(jsonPath("$[1]", Matchers.aMapWithSize(2)))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[1].name", Matchers.equalTo("Second origin")))
                .andReturn();
        logger.debug("Result:" + result.getResponse().getContentAsString());
    }
}
