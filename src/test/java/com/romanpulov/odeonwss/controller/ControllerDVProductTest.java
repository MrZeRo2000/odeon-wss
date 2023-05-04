package com.romanpulov.odeonwss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.odeonwss.builder.dtobuilder.DVCategoryDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.DVOriginDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.DVProductDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVCategoryBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVProductBuilder;
import com.romanpulov.odeonwss.dto.DVOriginDTO;
import com.romanpulov.odeonwss.dto.DVProductDTO;
import com.romanpulov.odeonwss.dto.DVProductDTOImpl;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.DVCategoryRepository;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import com.romanpulov.odeonwss.service.DVOriginService;
import com.romanpulov.odeonwss.service.DVProductService;
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

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerDVProductTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerDVProductTest.class);

    final ObjectMapper mapper = new ObjectMapper();

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

    @Autowired
    private DVOriginService dvOriginService;

    @Autowired
    private DVProductService dvProductService;

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
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(8)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].dvOrigin", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$[0].dvOrigin.name", Matchers.equalTo("Product Origin")))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("Green")))
                .andExpect(jsonPath("$[0].originalTitle", Matchers.equalTo("Original green")))
                .andExpect(jsonPath("$[0].year", Matchers.equalTo(1999)))
                .andExpect(jsonPath("$[0].hasDescription", Matchers.equalTo(true)))
                .andExpect(jsonPath("$[0].hasNotes", Matchers.equalTo(true)))
                .andExpect(jsonPath("$[0].dvCategories").isArray())
                .andExpect(jsonPath("$[0].dvCategories", Matchers.hasSize(0)))
                ;
        logger.debug("Get result:" + result.andReturn().getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void testGetDescriptionExistingOk() throws Exception {
        var result = mockMvc.perform(get("/api/dvproduct/description/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$.text", Matchers.equalTo("Green description")))
                ;
        logger.debug("Get result:" + result.andReturn().getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void testGetDescriptionNonExistingShouldFail() throws Exception {
        mockMvc.perform(get("/api/dvproduct/description/100"))
                .andExpect(status().isNotFound())
                ;
    }

    @Test
    @Order(2)
    void testGetNotesExistingOk() throws Exception {
        var result = mockMvc.perform(get("/api/dvproduct/notes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$.text", Matchers.equalTo("Green notes")))
                ;
        logger.debug("Get result:" + result.andReturn().getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void testGetNotesNonExistingShouldFail() throws Exception {
        mockMvc.perform(get("/api/dvproduct/notes/83"))
                .andExpect(status().isNotFound())
                ;
    }

    @Test
    @Order(3)
    void testGetTableAnimationWithCategories() throws Exception {
        var result = mockMvc.perform(get("/api/dvproduct/dvproducts/table")
                        .param("artifactTypeId", "203"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(6)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$[0].dvOrigin", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$[0].dvOrigin.name", Matchers.equalTo("Product Origin")))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("Brown")))
                .andExpect(jsonPath("$[0].hasDescription", Matchers.equalTo(false)))
                .andExpect(jsonPath("$[0].hasNotes", Matchers.equalTo(false)))
                .andExpect(jsonPath("$[0].dvCategories").isArray())
                .andExpect(jsonPath("$[0].dvCategories", Matchers.hasSize(0)))
                .andExpect(jsonPath("$[1]", Matchers.aMapWithSize(6)))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[1].dvOrigin", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$[1].dvOrigin.name", Matchers.equalTo("Product Origin")))
                .andExpect(jsonPath("$[1].title", Matchers.equalTo("White")))
                .andExpect(jsonPath("$[1].hasDescription", Matchers.equalTo(false)))
                .andExpect(jsonPath("$[1].hasNotes", Matchers.equalTo(false)))
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

    @Test
    @Order(10)
    void testInsertProductWithWrongArtifactTypeShouldFail() throws Exception {
        DVOriginDTO dvOriginDTO = dvOriginService.getById(1L);

        DVProductDTOImpl productDTO = new DVProductDTOBuilder()
                .withDvOrigin(dvOriginDTO)
                .withArtifactTypeId(987L)
                .withTitle("Wrong title")
                .build();

        String json = mapper.writeValueAsString(productDTO);
        logger.debug("insert json:" + json);

        var result = mockMvc.perform(post("/api/dvproduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("not found")));
        logger.debug("Post result:" + result.andReturn().getResponse().getContentAsString());
    }

    @Test
    @Order(11)
    void testInsertProductWithWrongOriginShouldFail() throws Exception {
        DVOriginDTO dvOriginDTO = new DVOriginDTOBuilder()
                .withId(388L)
                .withName("a name")
                .build();

        DVProductDTOImpl productDTO = new DVProductDTOBuilder()
                .withDvOrigin(dvOriginDTO)
                .withArtifactTypeId(203L)
                .withTitle("Wrong title with wrong origin")
                .build();

        String json = mapper.writeValueAsString(productDTO);
        logger.debug("insert json:" + json);

        var result = mockMvc.perform(post("/api/dvproduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("not found")));
        logger.debug("Post result:" + result.andReturn().getResponse().getContentAsString());
    }

    @Test
    @Order(12)
    void testInsertProductWithoutCategoriesShouldBeOk() throws Exception {
        DVOriginDTO dvOriginDTO = dvOriginService.getById(1L);

        DVProductDTOImpl productDTO = new DVProductDTOBuilder()
                .withDvOrigin(dvOriginDTO)
                .withArtifactTypeId(202L)
                .withTitle("Without Categories")
                .withOriginalTitle("Without Categories original")
                .withYear(2012L)
                .withFrontInfo("Front without categories")
                .withDescription("Description without categories")
                .withNotes("Notes without categories")
                .build();

        String json = mapper.writeValueAsString(productDTO);
        logger.debug("insert json:" + json);

        var result = mockMvc.perform(post("/api/dvproduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(10)))
                .andExpect(jsonPath("$.id", Matchers.equalTo(4)))
                .andExpect(jsonPath("$.dvOrigin.id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.dvOrigin.name", Matchers.equalTo("Product Origin")))
                .andExpect(jsonPath("$.title", Matchers.equalTo("Without Categories")))
                .andExpect(jsonPath("$.originalTitle", Matchers.equalTo("Without Categories original")))
                .andExpect(jsonPath("$.year", Matchers.equalTo(2012)))
                .andExpect(jsonPath("$.frontInfo", Matchers.equalTo("Front without categories")))
                .andExpect(jsonPath("$.description", Matchers.equalTo("Description without categories")))
                .andExpect(jsonPath("$.notes", Matchers.equalTo("Notes without categories")))
                .andExpect(jsonPath("$.dvCategories").isArray())
                .andExpect(jsonPath("$.dvCategories", Matchers.hasSize(0)))
        ;
        logger.debug("Post result:" + result.andReturn().getResponse().getContentAsString());
    }

    @Test
    @Order(13)
    void testInsertProductWithCategoriesShouldBeOk() throws Exception {
        DVOriginDTO dvOriginDTO = dvOriginService.getById(1L);

        DVProductDTOImpl productDTO = new DVProductDTOBuilder()
                .withDvOrigin(dvOriginDTO)
                .withArtifactTypeId(202L)
                .withTitle("With Categories")
                .withOriginalTitle("With Categories original")
                .withYear(2007L)
                .withFrontInfo("Front with categories")
                .withDescription("Description with categories")
                .withNotes("Notes with categories")
                .withDvCategories(List.of(
                        new DVCategoryDTOBuilder().withId(1L).build(),
                        new DVCategoryDTOBuilder().withId(2L).build()
                ))
                .build();

        String json = mapper.writeValueAsString(productDTO);
        logger.debug("insert json:" + json);

        var result = mockMvc.perform(post("/api/dvproduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(10)))
                .andExpect(jsonPath("$.id", Matchers.equalTo(5)))
                .andExpect(jsonPath("$.dvOrigin.id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.dvOrigin.name", Matchers.equalTo("Product Origin")))
                .andExpect(jsonPath("$.title", Matchers.equalTo("With Categories")))
                .andExpect(jsonPath("$.originalTitle", Matchers.equalTo("With Categories original")))
                .andExpect(jsonPath("$.year", Matchers.equalTo(2007)))
                .andExpect(jsonPath("$.frontInfo", Matchers.equalTo("Front with categories")))
                .andExpect(jsonPath("$.description", Matchers.equalTo("Description with categories")))
                .andExpect(jsonPath("$.notes", Matchers.equalTo("Notes with categories")))
                .andExpect(jsonPath("$.dvCategories").isArray())
                .andExpect(jsonPath("$.dvCategories", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.dvCategories[0].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$.dvCategories[0].name", Matchers.equalTo("Another category")))
                .andExpect(jsonPath("$.dvCategories[1].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.dvCategories[1].name", Matchers.equalTo("Some category")))
        ;
        logger.debug("Post result:" + result.andReturn().getResponse().getContentAsString());
    }

    @Test
    @Order(14)
    void testUpdateProductRemoveCategoriesShouldBeOk() throws Exception {
        DVProductDTO productDTO = dvProductService.getById(5L);
        productDTO.getDvCategories().clear();

        String json = mapper.writeValueAsString(productDTO);
        logger.debug("insert json:" + json);

        var result = mockMvc.perform(put("/api/dvproduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(10)))
                .andExpect(jsonPath("$.id", Matchers.equalTo(5)))
                .andExpect(jsonPath("$.dvOrigin.id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.dvOrigin.name", Matchers.equalTo("Product Origin")))
                .andExpect(jsonPath("$.title", Matchers.equalTo("With Categories")))
                .andExpect(jsonPath("$.originalTitle", Matchers.equalTo("With Categories original")))
                .andExpect(jsonPath("$.year", Matchers.equalTo(2007)))
                .andExpect(jsonPath("$.frontInfo", Matchers.equalTo("Front with categories")))
                .andExpect(jsonPath("$.description", Matchers.equalTo("Description with categories")))
                .andExpect(jsonPath("$.notes", Matchers.equalTo("Notes with categories")))
                .andExpect(jsonPath("$.dvCategories").isArray())
                .andExpect(jsonPath("$.dvCategories", Matchers.hasSize(0)))
        ;
        logger.debug("Put result:" + result.andReturn().getResponse().getContentAsString());
    }

    @Test
    @Order(15)
    void testDeleteProductWithCategories() throws Exception {
        DVProductDTO productDTO = dvProductService.getById(2L);
        assertThat(productDTO.getId()).isEqualTo(2);

        mockMvc.perform(get("/api/dvproduct/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var result = mockMvc.perform(delete("/api/dvproduct/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        logger.debug("Delete result:" + result.andReturn().getResponse().getContentAsString());

        mockMvc.perform(get("/api/dvproduct/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
