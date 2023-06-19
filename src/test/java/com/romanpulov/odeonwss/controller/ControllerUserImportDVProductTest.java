package com.romanpulov.odeonwss.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.odeonwss.builder.dtobuilder.DVCategoryDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.DVProductUserImportDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.DVProductUserImportDetailDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVCategoryBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVProductBuilder;
import com.romanpulov.odeonwss.entity.ArtifactType;
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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerUserImportDVProductTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerUserImportDVProductTest.class);

    List<String> PRODUCT_NAMES = List.of(
            "The Idol",
            "Cruel Summer"
    );

    final static ObjectMapper mapper = new ObjectMapper();
    static
    {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    DVOriginRepository dvOriginRepository;

    @Autowired
    DVCategoryRepository dvCategoryRepository;

    @Autowired
    DVProductRepository dvProductRepository;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    private ArtifactType getArtifactType() {
        return artifactTypeRepository.getWithDVMovies();
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(1)
    @Rollback(value = false)
    void testGenerateTestData() {
        DVOrigin dvOrigin = dvOriginRepository.save((new EntityDVOriginBuilder()).withName("Greece").build());
        PRODUCT_NAMES.forEach(s -> dvProductRepository.save(
                (new EntityDVProductBuilder())
                        .withOrigin(dvOrigin)
                        .withArtifactType(getArtifactType())
                        .withTitle(s)
                        .build()
        ));
        dvCategoryRepository.saveAll(List.of(
                (new EntityDVCategoryBuilder()).withName("Cat 01").build(),
                (new EntityDVCategoryBuilder()).withName("Cat 02").build()
        ));
    }

    @Test
    @Order(2)
    void testNoArtifactTypeShouldFail() throws Exception {
        mockMvc.perform(post("/api/dvproduct-user-import/analyze")
                    .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void testNoDvOriginShouldFail() throws Exception {
        var dataNoOrigin = (new DVProductUserImportDTOBuilder())
                .withArtifactTypeId(getArtifactType().getId())
                .build();

        String json = mapper.writeValueAsString(dataNoOrigin);
        logger.debug("no origin json:" + json);

        mockMvc.perform(post("/api/dvproduct-user-import/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void testWrongCategoriesShouldFail() throws Exception {
        var dataWrongCategories = (new DVProductUserImportDTOBuilder())
                .withArtifactTypeId(getArtifactType().getId())
                .withDvOriginId(dvOriginRepository.findById(1L).orElseThrow().getId())
                .withDvCategories(List.of(
                        (new DVCategoryDTOBuilder()).withName("Cat 01").build(),
                        (new DVCategoryDTOBuilder()).withName("Unknown Cat").build()
                ))
                .build();

        String json = mapper.writeValueAsString(dataWrongCategories);
        logger.debug("wrong categories json:" + json);

        mockMvc.perform(post("/api/dvproduct-user-import/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    void testInsertOneShouldBeOk() throws Exception {
        var data = (new DVProductUserImportDTOBuilder())
                .withArtifactTypeId(getArtifactType().getId())
                .withDvOriginId(dvOriginRepository.findById(1L).orElseThrow().getId())
                .withDvCategories(
                        List.of(
                                (new DVCategoryDTOBuilder()).withName("Cat 01").build()
                        )
                )
                .withDvProductDetails(List.of(
                        (new DVProductUserImportDetailDTOBuilder().withTitle("New Title").withOriginalTitle("New Original Title").withYear(2001L).build())
                ))
                .build();

        String json = mapper.writeValueAsString(data);
        logger.debug("ok data json:" + json);

        var result = mockMvc.perform(post("/api/dvproduct-user-import/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rowsInserted").isArray())
                .andExpect(jsonPath("$.rowsInserted", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.rowsInserted[0]", Matchers.equalTo("New Title")))
                .andExpect(jsonPath("$.rowsUpdated").isArray())
                .andExpect(jsonPath("$.rowsUpdated", Matchers.hasSize(0)))
                .andReturn();
        logger.debug("Post result:" + result.getResponse().getContentAsString());

    }

}
