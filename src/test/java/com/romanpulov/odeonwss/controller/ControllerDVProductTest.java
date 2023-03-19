package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVProductBuilder;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerDVProductTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerDVProductTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private DVOriginRepository dvOriginRepository;

    @Autowired
    private DVProductRepository dvProductRepository;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(1)
    @Rollback(value = false)
    void testGenerateTestData() {
        logger.debug("Generating data");
        // create product
        DVOrigin origin = dvOriginRepository.save(
                new EntityDVOriginBuilder()
                        .withId(1)
                        .withName("Product Origin")
                        .build()
        );

        dvProductRepository.save(new EntityDVProductBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
                .withOrigin(origin)
                .withTitle("Green")
                .build());
        dvProductRepository.save(new EntityDVProductBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
                .withOrigin(origin)
                .withTitle("White")
                .build());
        dvProductRepository.save(new EntityDVProductBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
                .withOrigin(origin)
                .withTitle("Brown")
                .build());
        logger.debug("Data generated");
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void testValidateGetAllIdTitleExistingOk() throws Exception {
        var result_202 = mockMvc.perform(get("/api/dvproduct/dvproducts/table-id-name")
                        .param("artifactTypeId", "202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$[0].title", Matchers.containsString("Brown")))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[1].title", Matchers.containsString("Green")))
                .andExpect(jsonPath("$[2].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[2].title", Matchers.containsString("White")))
        ;
        logger.debug("Get 202 result:" + result_202.andReturn().getResponse().getContentAsString());

        var result_203 = mockMvc.perform(get("/api/dvproduct/dvproducts/table-id-name")
                        .param("artifactTypeId", "203"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        logger.debug("Get 203 result:" + result_203.andReturn().getResponse().getContentAsString());
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void testValidateGetAllIdTitleNotExistingFailed() throws Exception {
        mockMvc.perform(get("/api/dvproduct/dvproducts/table-id-name")
                        .param("artifactTypeId", "1024"))
                .andExpect(status().isNotFound())
        ;
    }
}
