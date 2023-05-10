package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistCategoryBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistDetailBuilder;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.repository.ArtistCategoryRepository;
import com.romanpulov.odeonwss.repository.ArtistDetailRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerArtistTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerArtistTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistCategoryRepository artistCategoryRepository;

    @Autowired
    private ArtistDetailRepository artistDetailRepository;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(1)
    void testGenerateTestData() {
        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name23").build());
        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name76").build());
        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name55").build());

        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.CLASSICS).withName("Classics001").build());
    }

    @Test
    @Order(2)
    void testValidateGetAllIdName() throws Exception {
        mockMvc.perform(get("/api/artist/artists/table-id-name").param("artistTypeCode", "A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].name", Matchers.containsString("Name23")))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$[1].name", Matchers.containsString("Name55")))
                .andExpect(jsonPath("$[2].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[2].name", Matchers.containsString("Name76")))
        ;
        mockMvc.perform(get("/api/artist/artists/table-id-name").param("artistTypeCode", "C"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(4)))
                .andExpect(jsonPath("$[0].name", Matchers.containsString("Classics001")))
        ;
    }

    @Test
    @Order(3)
    void testGetTable() throws Exception {
        var result = mockMvc.perform(get("/api/artist/artists/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(4)))
                //
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(4)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(4)))
                .andExpect(jsonPath("$[0].artistName", Matchers.equalTo("Classics001")))
                .andExpect(jsonPath("$[0].artistType", Matchers.equalTo("C")))
                .andExpect(jsonPath("$[0].styles", Matchers.hasSize(0)))
                //
                .andExpect(jsonPath("$[1]", Matchers.aMapWithSize(4)))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[1].artistName", Matchers.equalTo("Name23")))
                .andExpect(jsonPath("$[1].artistType", Matchers.equalTo("A")))
                .andExpect(jsonPath("$[1].styles", Matchers.hasSize(0)))
                //
                .andExpect(jsonPath("$[2]", Matchers.aMapWithSize(4)))
                .andExpect(jsonPath("$[2].id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$[2].artistName", Matchers.equalTo("Name55")))
                .andExpect(jsonPath("$[2].artistType", Matchers.equalTo("A")))
                .andExpect(jsonPath("$[2].styles", Matchers.hasSize(0)))
                //
                .andExpect(jsonPath("$[3]", Matchers.aMapWithSize(4)))
                .andExpect(jsonPath("$[3].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[3].artistName", Matchers.equalTo("Name76")))
                .andExpect(jsonPath("$[3].artistType", Matchers.equalTo("A")))
                .andExpect(jsonPath("$[3].styles", Matchers.hasSize(0)))
                //
                .andReturn();
        logger.debug("Get result: " + result.getResponse().getContentAsString());
    }

    @Test
    @Order(4)
    void testGetTableWithCategories() throws Exception {
        var a1 = new EntityArtistBuilder()
                .withType(ArtistType.ARTIST)
                .withName("with Categories")
                .build();
        artistRepository.save(a1);


        var g1 = new EntityArtistCategoryBuilder()
                .withType(ArtistCategoryType.GENRE)
                .withName("Rock")
                .withArtist(a1)
                .build();
        artistCategoryRepository.save(g1);

        var s1 = new EntityArtistCategoryBuilder()
                .withType(ArtistCategoryType.STYLE)
                .withName("Heavy Metal")
                .withArtist(a1)
                .build();
        artistCategoryRepository.save(s1);

        var s2 = new EntityArtistCategoryBuilder()
                .withType(ArtistCategoryType.STYLE)
                .withName("Grunge")
                .withArtist(a1)
                .build();
        artistCategoryRepository.save(s2);

        var ad1 = new EntityArtistDetailBuilder()
                .withArtist(a1)
                .withBiography("Bio 1")
                .build();
        artistDetailRepository.save(ad1);

        var result = mockMvc.perform(get("/api/artist/artists/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(5)))
                //
                .andExpect(jsonPath("$[4]", Matchers.aMapWithSize(6)))
                .andExpect(jsonPath("$[4].id", Matchers.equalTo(5)))
                .andExpect(jsonPath("$[4].artistName", Matchers.equalTo("with Categories")))
                .andExpect(jsonPath("$[4].artistType", Matchers.equalTo("A")))
                .andExpect(jsonPath("$[4].genre", Matchers.equalTo("Rock")))
                .andExpect(jsonPath("$[4].styles", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[4].styles[0]", Matchers.equalTo("Grunge")))
                .andExpect(jsonPath("$[4].styles[1]", Matchers.equalTo("Heavy Metal")))
                .andExpect(jsonPath("$[4].detailId", Matchers.equalTo(1)))
                //
                .andReturn();
        logger.debug("Get result with categories: " + result.getResponse().getContentAsString());
    }
}
