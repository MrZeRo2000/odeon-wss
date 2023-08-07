package com.romanpulov.odeonwss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.odeonwss.builder.dtobuilder.MediaFileDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
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
public class ControllerMediaFileTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerMediaFileTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private ObjectMapper mapper;

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

        Artifact artifact1 = artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist1)
                        .withArtifactType(artifactTypeRepository.getWithLA())
                        .withTitle("Title 1")
                        .withYear(2001L)
                        .withDuration(12345L)
                        .build()
        );

        Artifact artifact2 = artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist1)
                        .withArtifactType(artifactTypeRepository.getWithLA())
                        .withTitle("Title 2")
                        .withYear(2001L)
                        .withDuration(4234L)
                        .build()
        );

        String json11 = mapper.writeValueAsString(
                new MediaFileDTOBuilder()
                        .withArtifactId(artifact1.getId())
                        .withName("Name 11")
                        .withFormat("ape")
                        .withBitrate(1000L)
                        .withSize(3423L)
                        .withDuration(52345L)
                        .build()
        );

        var result11 = this.mockMvc.perform(
                        post("/api/media-file").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json11)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("result 11:" + result11.getResponse().getContentAsString());

        String json12 = mapper.writeValueAsString(
                new MediaFileDTOBuilder()
                        .withArtifactId(artifact1.getId())
                        .withName("Name 12")
                        .withFormat("ape")
                        .withBitrate(1001L)
                        .withSize(3424L)
                        .build()
        );

        var result12 = this.mockMvc.perform(
                        post("/api/media-file").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json12)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("result 12:" + result12.getResponse().getContentAsString());

        String json21 = mapper.writeValueAsString(
                new MediaFileDTOBuilder()
                        .withArtifactId(artifact2.getId())
                        .withName("Name 21")
                        .withFormat("ape")
                        .withBitrate(1004L)
                        .withSize(3438L)
                        .withDuration(57428L)
                        .build()
        );

        var result21 = this.mockMvc.perform(
                        post("/api/media-file").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json21)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("result 21:" + result21.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void testGetTableArtifact1() throws Exception {
        var result = this.mockMvc.perform(get("/api/media-file/table/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(6)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo("Name 11")))
                .andExpect(jsonPath("$[0].format", Matchers.equalTo("ape")))
                .andExpect(jsonPath("$[0].bitrate", Matchers.equalTo(1000)))
                .andExpect(jsonPath("$[0].duration", Matchers.equalTo(52345)))
                .andExpect(jsonPath("$[0].size", Matchers.equalTo(3423)))
                .andExpect(jsonPath("$[1]", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[1].duration").doesNotExist())
                .andReturn()
        ;
        logger.debug("testGetTableArtifact1:" + result.getResponse().getContentAsString());

    }

    @Test
    @Order(2)
    void testGetTableArtifact2() throws Exception {
        this.mockMvc.perform(get("/api/media-file/table/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].size").exists())
        ;
    }

    @Test
    @Order(3)
    void testGetTableWrongArtifact() throws Exception {
        this.mockMvc.perform(get("/api/media-file/table/8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @Order(4)
    void testGetTableIdNameDuration() throws Exception {
        this.mockMvc.perform(get("/api/media-file/table-id-name-duration/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(3)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Name 11")))
                .andExpect(jsonPath("$[0].duration", Matchers.is(52345)))
                .andExpect(jsonPath("$[0].size").doesNotExist())
        ;
    }
}
