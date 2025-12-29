package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityTagBuilder;
import com.romanpulov.odeonwss.repository.TagRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerTagTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerTagTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagRepository tagRepository;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(1)
    @Rollback(value = false)
    void testGenerateTestData() {
        logger.debug("Generating data");
        Stream.of("Red", "Hot", "Chili", "Peppers")
                .forEach(v -> tagRepository.save(new EntityTagBuilder().withName(v).build()));

        assertThat(tagRepository.findAllDTO().size()).isEqualTo(4);
    }

    @Test
    @Order(2)
    void testValidateAllIdName() throws Exception {
        var result = mockMvc.perform(get("/api/tag/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(4)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo("Chili")))
                .andExpect(jsonPath("$[1]", Matchers.aMapWithSize(2)))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[1].name", Matchers.equalTo("Hot")))
                .andExpect(jsonPath("$[2]", Matchers.aMapWithSize(2)))
                .andExpect(jsonPath("$[2].id", Matchers.equalTo(4)))
                .andExpect(jsonPath("$[2].name", Matchers.equalTo("Peppers")))
                .andExpect(jsonPath("$[3]", Matchers.aMapWithSize(2)))
                .andExpect(jsonPath("$[3].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[3].name", Matchers.equalTo("Red")))
                .andReturn();
        logger.debug("Result:" + result.getResponse().getContentAsString());
    }
}
