package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
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

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(1)
    void testGenerateTestData() throws Exception {
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
}
