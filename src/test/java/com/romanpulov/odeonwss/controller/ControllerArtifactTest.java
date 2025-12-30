package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.builder.dtobuilder.ArtifactDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerArtifactTest {
    @SuppressWarnings("unused")
    final static Logger logger = LoggerFactory.getLogger(ControllerArtifactTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private JsonMapper mapper;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void prepareDataShouldBeOk() {
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

        Artist performerArtist1 = artistRepository.save(
                new EntityArtistBuilder()
                        .withType(ArtistType.CLASSICS)
                        .withName("Performer Artist 1")
                        .build()
        );

        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist1)
                        .withArtifactType(artifactTypeRepository.getWithLA())
                        .withTitle("Artist 1 LA Title 1")
                        .withYear(2000L)
                        .withDuration(7777L)
                        .build()
        );

        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist1)
                        .withArtifactType(artifactTypeRepository.getWithMP3())
                        .withTitle("Artist 1 MP3 Title 1")
                        .withYear(2001L)
                        .withDuration(6666L)
                        .build()
        );

        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist1)
                        .withArtifactType(artifactTypeRepository.getWithMP3())
                        .withTitle("Artist 1 MP3 Title 2")
                        .withYear(2002L)
                        .withDuration(5555L)
                        .build()
        );

        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(classics1)
                        .withPerformerArtist(performerArtist1)
                        .withArtifactType(artifactTypeRepository.getWithMP3())
                        .withTitle("Classics Artist 1 Classics Title 1")
                        .withYear(1975L)
                        .withDuration(46534L)
                        .build()
        );

        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtifactType(artifactTypeRepository.getWithDVMovies())
                        .withTitle("With tags")
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
                        .queryParam("artifactTypeCodes", "101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].artist.artistName", Matchers.equalTo("Classics 1")))
                .andExpect(jsonPath("$[0].performerArtist.artistName", Matchers.equalTo("Performer Artist 1")))
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

    @Test
    @Order(21)
    void testUpdateTags() throws Exception {
        var artifactTagsWrongArtifact = new ArtifactDTOBuilder()
                .withId(955L)
                .withTags(List.of("Warm", "Cold", "Wet"))
                .build();
        String jsonWrongArtifact = mapper.writeValueAsString(artifactTagsWrongArtifact);

        this.mockMvc.perform(put("/api/artifact/update-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWrongArtifact)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        var artifactTagsOk = new ArtifactDTOBuilder()
                .withId(5L)
                .withTags(List.of("Warm", "Cold", "Wet"))
                .build();
        String jsonOk = mapper.writeValueAsString(artifactTagsOk);

        this.mockMvc.perform(put("/api/artifact/update-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonOk)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(5)))
                .andExpect(jsonPath("$.tags", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.tags[0]", Matchers.is("Cold")))
                .andExpect(jsonPath("$.tags[1]", Matchers.is("Warm")))
                .andExpect(jsonPath("$.tags[2]", Matchers.is("Wet")))
        ;

        var artifactDeleteOne = new ArtifactDTOBuilder()
                .withId(5L)
                .withTags(List.of("Cold", "Wet"))
                .build();
        String jsonDeleteOne = mapper.writeValueAsString(artifactDeleteOne);

        this.mockMvc.perform(put("/api/artifact/update-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDeleteOne)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(5)))
                .andExpect(jsonPath("$.tags", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.tags[0]", Matchers.is("Cold")))
                .andExpect(jsonPath("$.tags[1]", Matchers.is("Wet")))
        ;

        var artifactDeleteAll = new ArtifactDTOBuilder()
                .withId(5L)
                .build();
        String jsonDeleteAll = mapper.writeValueAsString(artifactDeleteAll);

        this.mockMvc.perform(put("/api/artifact/update-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDeleteAll)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                /*.andExpect(jsonPath("$.id").doesNotExist())*/
                .andExpect(jsonPath("$.tags", Matchers.hasSize(0)))
        ;
    }

    @Test
    @Order(31)
    void testGetTableByOptional() throws Exception {
        var artifactTagsOk = new ArtifactDTOBuilder()
                .withId(5L)
                .withTags(List.of("Red", "Green"))
                .build();
        String jsonOk = mapper.writeValueAsString(artifactTagsOk);

        this.mockMvc.perform(put("/api/artifact/update-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonOk)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var resultNoArgs = this.mockMvc.perform(get("/api/artifact/table-by-optional")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(5)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testGetTableByOptional resultNoArgs:{}", resultNoArgs);

        var resultByArtifactTypeId = this.mockMvc.perform(get("/api/artifact/table-by-optional")
                        .queryParam("artifactTypeIds", "101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testGetTableByOptional resultByArtifactTypeId:{}", resultByArtifactTypeId);

        var resultByArtistId = this.mockMvc.perform(get("/api/artifact/table-by-optional")
                        .queryParam("artistIds", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testGetTableByOptional resultByArtistId:{}", resultByArtistId);

        var resultByArtifactTypeIdAndArtistId = this.mockMvc.perform(get("/api/artifact/table-by-optional")
                        .queryParam("artifactTypeIds", "101")
                        .queryParam("artistIds", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testGetTableByOptional resultByArtifactTypeIdAndArtistId:{}", resultByArtifactTypeIdAndArtistId);

        var resultByAllArtifactTypeIds = this.mockMvc.perform(get("/api/artifact/table-by-optional")
                        .queryParam("artifactTypeIds", "101")
                        .queryParam("artifactTypeIds", "202")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(4)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testGetTableByOptional resultByAllArtifactTypeIds:{}", resultByAllArtifactTypeIds);
    }
}
