package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVCategoryBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVProductBuilder;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.DVCategoryRepository;
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

import java.util.List;
import java.util.stream.Collectors;

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
    private DVCategoryRepository dvCategoryRepository;

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

        dvCategoryRepository.saveAll(
                List.of(
                        new EntityDVCategoryBuilder()
                                .withName("Some category")
                                .build(),
                        new EntityDVCategoryBuilder()
                                .withName("Another category")
                                .build(),
                        new EntityDVCategoryBuilder()
                                .withName("Third category")
                                .build()
                )
        );

        dvProductRepository.save(new EntityDVProductBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
                .withOrigin(origin)
                .withTitle("Green")
                .withOriginalTitle("Original green")
                .withYear(1999L)
                .withDescription("Green description")
                .withNotes("Green notes")
                .build());
        dvProductRepository.save(new EntityDVProductBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVAnimation())
                .withOrigin(origin)
                .withTitle("White")
                .withCategories(dvCategoryRepository.findAll()
                        .stream()
                        .filter(c -> c.getId() == 2L || c.getId() == 3L)
                        .collect(Collectors.toSet()))
                .build());
        dvProductRepository.save(new EntityDVProductBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVAnimation())
                .withOrigin(origin)
                .withTitle("Brown")
                .build());
        logger.debug("Data generated");
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void testValidateGetAllIdTitleExistingOk() throws Exception {
        var result_202 = mockMvc.perform(get("/api/dvproduct/dvproducts/table-id-title")
                        .param("artifactTypeId", "202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].title", Matchers.containsString("Green")))
        ;
        logger.debug("Get 202 result:" + result_202.andReturn().getResponse().getContentAsString());

        var result_203 = mockMvc.perform(get("/api/dvproduct/dvproducts/table-id-title")
                        .param("artifactTypeId", "203"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$[0].title", Matchers.containsString("Brown")))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[1].title", Matchers.containsString("White")))
        ;
        logger.debug("Get 203 result:" + result_203.andReturn().getResponse().getContentAsString());

        var result_204 = mockMvc.perform(get("/api/dvproduct/dvproducts/table-id-title")
                        .param("artifactTypeId", "204"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        logger.debug("Get 204 result:" + result_204.andReturn().getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void testValidateGetAllIdTitleNotExistingFailed() throws Exception {
        mockMvc.perform(get("/api/dvproduct/dvproducts/table-id-title")
                        .param("artifactTypeId", "1024"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @Order(2)
    void testGetTableMoviesNoCategories() throws Exception {
        var result = mockMvc.perform(get("/api/dvproduct/dvproducts/table")
                        .param("artifactTypeId", "202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(6)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].dvOrigin", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$[0].dvOrigin.name", Matchers.equalTo("Product Origin")))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("Green")))
                .andExpect(jsonPath("$[0].originalTitle", Matchers.equalTo("Original green")))
                .andExpect(jsonPath("$[0].year", Matchers.equalTo(1999)))
                .andExpect(jsonPath("$[0].dvCategories").isArray())
                .andExpect(jsonPath("$[0].dvCategories", Matchers.hasSize(0)))
                ;
        logger.debug("Get result:" + result.andReturn().getResponse().getContentAsString());
    }

    @Test
    @Order(3)
    void testGetTableAnimationWithCategories() throws Exception {
        var result = mockMvc.perform(get("/api/dvproduct/dvproducts/table")
                        .param("artifactTypeId", "203"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(4)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$[0].dvOrigin", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$[0].dvOrigin.name", Matchers.equalTo("Product Origin")))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("Brown")))
                .andExpect(jsonPath("$[0].dvCategories").isArray())
                .andExpect(jsonPath("$[0].dvCategories", Matchers.hasSize(0)))
                .andExpect(jsonPath("$[1]", Matchers.aMapWithSize(4)))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[1].dvOrigin", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$[1].dvOrigin.name", Matchers.equalTo("Product Origin")))
                .andExpect(jsonPath("$[1].title", Matchers.equalTo("White")))
                .andExpect(jsonPath("$[1].dvCategories").isArray())
                .andExpect(jsonPath("$[1].dvCategories", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[1].dvCategories[0]", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$[1].dvCategories[0].name", Matchers.equalTo("Another category")))
                .andExpect(jsonPath("$[1].dvCategories[1]", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$[1].dvCategories[1].name", Matchers.equalTo("Third category")))

                ;
        logger.debug("Get result:" + result.andReturn().getResponse().getContentAsString());
    }

    @Test
    @Order(4)
    void testGetTableNoData() throws Exception {
        var result = mockMvc.perform(get("/api/dvproduct/dvproducts/table")
                        .param("artifactTypeId", "204"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(0)))
                ;
        logger.debug("Get result:" + result.andReturn().getResponse().getContentAsString());
    }

}
