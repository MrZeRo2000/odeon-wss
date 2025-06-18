package com.romanpulov.odeonwss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.odeonwss.builder.dtobuilder.DVCategoryDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVCategoryBuilder;
import com.romanpulov.odeonwss.dto.DVCategoryDTOImpl;
import com.romanpulov.odeonwss.repository.DVCategoryRepository;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerDVCategoryTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerDVCategoryTest.class);

    final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DVCategoryRepository repository;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(1)
    @Rollback(value = false)
    void testGenerateTestData() {
        logger.debug("Generating data");

        repository.save(
                new EntityDVCategoryBuilder()
                        .withName("Second Category")
                        .build()
        );

        repository.save(
                new EntityDVCategoryBuilder()
                        .withName("First Category")
                        .build()
        );

        assertThat(repository.findAllDTO().size()).isEqualTo(2);
    }

    @Test
    @Order(2)
    void testValidateAllIdName() throws Exception {
        var result = mockMvc.perform(get("/api/dvcategory/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo("First Category")))
                .andExpect(jsonPath("$[1]", Matchers.aMapWithSize(2)))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[1].name", Matchers.equalTo("Second Category")))
                .andReturn();
        logger.debug("Result:" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(3)
    void testInsertNotExistingShouldBeOk() throws Exception {
        DVCategoryDTOImpl Category = new DVCategoryDTOBuilder()
                .withName("Category inserted")
                .build();

        String json = mapper.writeValueAsString(Category);
        logger.debug("insert json:" + json);

        var result = this.mockMvc.perform(post("/api/dvcategory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(2)))
                .andExpect(jsonPath("$.id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.name", Matchers.equalTo("Category inserted")))
                .andReturn();
        logger.debug("Result:" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(4)
    void testInsertExistingShouldFail() throws Exception {
        DVCategoryDTOImpl Category = new DVCategoryDTOBuilder()
                .withId(3L)
                .withName("Category inserted new")
                .build();

        String json = mapper.writeValueAsString(Category);
        logger.debug("insert json:" + json);

        this.mockMvc.perform(post("/api/dvcategory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(5)
    void testUpdateExistingShouldBeOk() throws Exception {
        DVCategoryDTOImpl Category = new DVCategoryDTOBuilder()
                .withId(3L)
                .withName("Category updated")
                .build();

        String json = mapper.writeValueAsString(Category);
        logger.debug("update json:" + json);

        var result = this.mockMvc.perform(put("/api/dvcategory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(2)))
                .andExpect(jsonPath("$.id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.name", Matchers.equalTo("Category updated")))
                .andReturn();
        logger.debug("Result:" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(6)
    void updateNotExistingShouldFail() throws Exception {
        DVCategoryDTOImpl Category = new DVCategoryDTOBuilder()
                .withId(76L)
                .withName("Category updated")
                .build();

        String json = mapper.writeValueAsString(Category);
        logger.debug("update json:" + json);

        this.mockMvc.perform(put("/api/dvcategory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    void testDeleteExistingShouldBeOk() throws Exception {
        mockMvc.perform(get("/api/dvcategory/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(3)));

        var result = this.mockMvc.perform(delete("/api/dvcategory/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        logger.debug("Result:" + result.getResponse().getContentAsString());

        mockMvc.perform(get("/api/dvcategory/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    @Order(8)
    void testDeleteNotExistingShouldFail() throws Exception {
        mockMvc.perform(get("/api/dvcategory/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));

        this.mockMvc.perform(delete("/api/dvcategory/63")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/dvcategory/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

}
