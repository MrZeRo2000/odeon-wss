package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityMediaFileBuilder;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.MediaFileService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerMediaFileTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private MediaFileService mediaFileService;

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
                        .withArtifactType(ArtifactType.withLA())
                        .withTitle("Title 1")
                        .withYear(2001L)
                        .withDuration(12345L)
                        .build()
        );

        Artifact artifact2 = artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist1)
                        .withArtifactType(ArtifactType.withLA())
                        .withTitle("Title 2")
                        .withYear(2001L)
                        .withDuration(4234L)
                        .build()
        );

        mediaFileRepository.save(
                new EntityMediaFileBuilder()
                        .withArtifact(artifact1)
                        .withName("Name 11")
                        .withFormat("ape")
                        .withBitrate(1000L)
                        .withSize(3423L)
                        .withDuration(52345L)
                        .build()
        );
        mediaFileRepository.save(
                new EntityMediaFileBuilder()
                        .withArtifact(artifact1)
                        .withName("Name 12")
                        .withFormat("ape")
                        .withBitrate(1001L)
                        .withSize(3424L)
                        .withDuration(57474L)
                        .build()
        );


        mediaFileRepository.save(
                new EntityMediaFileBuilder()
                        .withArtifact(artifact2)
                        .withName("Name 21")
                        .withFormat("ape")
                        .withBitrate(1004L)
                        .withSize(3438L)
                        .withDuration(57428L)
                        .build()
        );
    }

    @Test
    @Order(2)
    void testGetTableArtifact1() throws Exception {
        this.mockMvc.perform(get("/api/media-file/table")
                        .queryParam("artifactId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
        ;

    }

    @Test
    @Order(2)
    void testGetTableArtifact2() throws Exception {
        this.mockMvc.perform(get("/api/media-file/table")
                        .queryParam("artifactId", "2")
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
        this.mockMvc.perform(get("/api/media-file/table")
                        .queryParam("artifactId", "8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @Order(4)
    void testGetTableIdName() throws Exception {
        this.mockMvc.perform(get("/api/media-file/table-id-name")
                        .queryParam("artifactId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Name 11")))
                .andExpect(jsonPath("$[0].size").doesNotExist())
        ;
    }
}
