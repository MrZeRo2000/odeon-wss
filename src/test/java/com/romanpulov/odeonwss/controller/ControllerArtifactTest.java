package com.romanpulov.odeonwss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerArtifactTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerArtifactTest.class);

    final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void prepareDataShouldBeOk() throws Exception {
        Artist artist1 = artistRepository.save(
                new EntityArtistBuilder()
                        .withType(ArtistType.ARTIST)
                        .withName("Artist 1")
                        .build()
        );

        Artist classics1 = artistRepository.save(
                new EntityArtistBuilder()
                        .withType(ArtistType.CLASSICS)
                        .withName("Classics 1")
                        .build()
        );

        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist1)
                        .withArtifactType(ArtifactType.withLA())
                        .withTitle("Artist 1 LA Title 1")
                        .withYear(2000L)
                        .withDuration(7777L)
                        .build()
        );

        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist1)
                        .withArtifactType(ArtifactType.withMP3())
                        .withTitle("Artist 1 MP3 Title 1")
                        .withYear(2001L)
                        .withDuration(6666L)
                        .build()
        );

        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist1)
                        .withArtifactType(ArtifactType.withMP3())
                        .withTitle("Artist 1 MP3 Title 2")
                        .withYear(2002L)
                        .withDuration(5555L)
                        .build()
        );
    }

    @Test
    @Order(2)
    void testGetTableMP3LA() throws Exception {
        this.mockMvc.perform(get("/api/artifact/table")
                        .queryParam("artistTypeCode", "A")
                        .queryParam("artifactTypeCodes", "101", "102")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
        ;
    }

    @Test
    @Order(3)
    void testGetTableMP3() throws Exception {
        this.mockMvc.perform(get("/api/artifact/table")
                        .queryParam("artistTypeCode", "A")
                        .queryParam("artifactTypeCodes", "101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
        ;
    }

    @Test
    @Order(4)
    void testGetTableLA() throws Exception {
        this.mockMvc.perform(get("/api/artifact/table")
                        .queryParam("artistTypeCode", "A")
                        .queryParam("artifactTypeCodes", "102")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
        ;
    }

    @Test
    @Order(5)
    void testGetTableClassics() throws Exception {
        this.mockMvc.perform(get("/api/artifact/table")
                        .queryParam("artistTypeCode", "C")
                        .queryParam("artifactTypeCodes", "102")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(0)))
        ;
    }

    @Test
    @Order(6)
    void testGetTableNoTypes() throws Exception {
        this.mockMvc.perform(get("/api/artifact/table")
                        .queryParam("artistTypeCode", "A")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @Order(7)
    void testGetTableWrongArtist() throws Exception {
        this.mockMvc.perform(get("/api/artifact/table")
                        .queryParam("artistTypeCode", "D")
                        .queryParam("artifactTypeCodes", "LA")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
        ;
    }
}
