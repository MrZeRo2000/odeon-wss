package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.builder.dtobuilder.*;
import com.romanpulov.odeonwss.builder.entitybuilder.*;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerTrackTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerTrackTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private DVOriginRepository dvOriginRepository;

    @Autowired
    private DVProductRepository dvProductRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private JsonMapper mapper;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(1)
    @Rollback(value = false)
    void testGenerateTestData() throws Exception {
        logger.debug("Generating data");
        // create product
        DVOrigin origin = dvOriginRepository.save(
                new EntityDVOriginBuilder()
                        .withName("Product Origin")
                        .build()
        );

        dvProductRepository.save(new EntityDVProductBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
                .withOrigin(origin)
                .withTitle("Small")
                .build());
        dvProductRepository.save(new EntityDVProductBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVAnimation())
                .withOrigin(origin)
                .withTitle("Big")
                .build());

        Artifact artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
                .withTitle("Title 1")
                .build();
        artifactRepository.save(artifact);
        assertThat(artifact.getId()).isGreaterThan(0);

        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withName("Media file")
                .withFormat("MKV")
                .withBitrate(2000L)
                .withSize(484848L)
                .withDuration(800L)
                .build();
        mediaFileRepository.save(mediaFile);

        DVProduct dvProduct = dvProductRepository.findById(2L).orElseThrow();

        String json = mapper.writeValueAsString(
                new TrackDTOBuilder()
                        .withArtifactId(artifact.getId())
                        .withTitle("Track title")
                        .withDiskNum(1L)
                        .withNum(8L)
                        .withDuration(6665L)
                        .withDvTypeId(7L)
                        .withDvProductId(dvProduct.getId())
                        .withMediaFileIds(List.of(mediaFile.getId()))
                        .build()
        );

        var result = this.mockMvc.perform(
                        post("/api/track").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testGenerateTestData result:{}", result);

        var artist = new EntityArtistBuilder()
                .withType(ArtistType.ARTIST)
                .withName("Jimmy")
                .build();
        artistRepository.save(artist);

        var musicArtifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithMP3())
                .withArtist(artist)
                .withTitle("Music title")
                .withDuration(65432L)
                .build();
        artifactRepository.save(musicArtifact);

        var musicTrack = new EntityTrackBuilder()
                .withArtifact(musicArtifact)
                .withArtist(artist)
                .withTitle("Music track title")
                .withDuration(5233L)
                .build();
        trackRepository.save(musicTrack);

        logger.debug("Data generated");
    }

    @Test
    @Order(2)
    void testGetTableByProductId() throws Exception {
        var result_1 = mockMvc.perform(get("/api/track/table")
                        .param("dvProductId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.debug("testGetTableByProductId Get 1 result:{}", result_1);

        var result_2 = mockMvc.perform(get("/api/track/table")
                        .param("dvProductId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(11)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].artifact.id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].artifact.title", Matchers.equalTo("Title 1")))
                .andExpect(jsonPath("$[0].dvType.id", Matchers.equalTo(7)))
                .andExpect(jsonPath("$[0].dvType.name", Matchers.equalTo("AVC")))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("Track title")))
                .andExpect(jsonPath("$[0].duration", Matchers.equalTo(6665)))
                .andExpect(jsonPath("$[0].size", Matchers.equalTo(484848)))
                .andExpect(jsonPath("$[0].bitRate", Matchers.equalTo(2000)))
                .andExpect(jsonPath("$[0].num", Matchers.equalTo(8)))
                .andExpect(jsonPath("$[0].mediaFiles").isArray())
                .andExpect(jsonPath("$[0].mediaFiles", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].dvProduct.id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[0].dvProduct.title", Matchers.equalTo("Big")))
                .andExpect(jsonPath("$[0].dvProduct.dvCategories").isArray())
                .andExpect(jsonPath("$[0].dvProduct.dvCategories").isEmpty())
                .andExpect(jsonPath("$[0].tags").isArray())
                .andExpect(jsonPath("$[0].tags").isEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString()
                ;
        logger.debug("testGetTableByProductId Get 2 result:{}", result_2);
    }

    @Test
    @Order(2)
    void testGetTableByArtifactId() throws Exception {
        var result_1 = mockMvc.perform(get("/api/track/table/10"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.debug("testGetTableByArtifactId Get 1 result:{}", result_1);

        var result_2 = mockMvc.perform(get("/api/track/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(11)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].dvType.id", Matchers.equalTo(7)))
                .andExpect(jsonPath("$[0].dvType.name", Matchers.equalTo("AVC")))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("Track title")))
                .andExpect(jsonPath("$[0].duration", Matchers.equalTo(6665)))
                .andExpect(jsonPath("$[0].diskNum", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].num", Matchers.equalTo(8)))
                .andExpect(jsonPath("$[0].size", Matchers.equalTo(484848)))
                .andExpect(jsonPath("$[0].bitRate", Matchers.equalTo(2000)))
                .andExpect(jsonPath("$[0].mediaFiles").isArray())
                .andExpect(jsonPath("$[0].mediaFiles", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].dvProduct.id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[0].dvProduct.title").doesNotExist())
                .andExpect(jsonPath("$[0].dvProduct.dvCategories").isArray())
                .andExpect(jsonPath("$[0].dvProduct.dvCategories").isEmpty())
                .andExpect(jsonPath("$[0].tags").isArray())
                .andExpect(jsonPath("$[0].tags").isEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString()
                ;
        logger.debug("testGetTableByArtifactId Get 2 result:{}", result_2);
    }

    @Test
    @Order(10)
    void testUIDataPost() throws Exception {
        var json = """
            {"artifact":{"id":1},"diskNum":1,"num":13,"artist":{},"performerArtist":{},"dvType":{},"title":"34","duration":11,"mediaFiles":[{"id":1},{"id":2}],"dvProduct":{}}
            """;
        var result = this.mockMvc.perform(
                        post("/api/track").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testUIDataPost result:{}", result);
    }

    @Test
    @Order(11)
    void testResetTrackNumbers() throws Exception {
        var result_before = mockMvc.perform(get("/api/track/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].num", Matchers.equalTo(8)))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$[1].num", Matchers.equalTo(13)))
                .andReturn()
                .getResponse()
                .getContentAsString()
                ;
        logger.debug("testResetTrackNumbers Get before result:{}", result_before);

        // adjust data - remove disk numbers
        trackRepository.findAll().forEach(t -> {
            t.setDiskNum(null);
            trackRepository.save(t);
        });

        var result = this.mockMvc.perform(
                        post("/api/track/reset-track-numbers/1").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$.rowsAffected", Matchers.equalTo(2)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testResetTrackNumbers result:{}", result);

        var result_after = mockMvc.perform(get("/api/track/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].num", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$[1].num", Matchers.equalTo(2)))
                .andReturn()
                .getResponse()
                .getContentAsString()
                ;
        logger.debug("testResetTrackNumbers Get after result:{}", result_after);
    }

    @Test
    @Order(12)
    void testUpdateTrackDurations() throws Exception {
        var json = """
                {"artifact":{"id":1}, "mediaFile":{"id":1}, "chapters":["00:01:55"]}
                """;
        var result = this.mockMvc.perform(
                        post("/api/track/update-track-durations").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$.rowsAffected", Matchers.equalTo(2)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testUpdateTrackDurations testUpdateTrackDurations result:{}", result);

        var result_after = mockMvc.perform(get("/api/track/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].duration", Matchers.equalTo(60 + 55)))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$[1].duration", Matchers.equalTo(800 - 60 - 55)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testUpdateTrackDurations testUpdateTrackDurations tracks after:{}", result_after);
    }

    @Test
    @Order(13)
    void testUpdateVideoTypesWrongParams() throws Exception {
        // wrong id entity not found
        String json = mapper.writeValueAsString(new TrackDVTypeUserUpdateDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(5).build())
                .withDVType(new IdNameDTOBuilder().withId(8).build())
                .build());
        logger.info("testUpdateVideoTypes update object:{}", json);

        this.mockMvc.perform(
            post("/api/track/update-track-video-types").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

        // wrong id entity not found
        json = mapper.writeValueAsString(new TrackDVTypeUserUpdateDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1).build())
                .withDVType(new IdNameDTOBuilder().withId(83).build())
                .build());
        logger.info("testUpdateVideoTypesWrongParams testUpdateVideoTypes update object:{}", json);

        this.mockMvc.perform(
            post("/api/track/update-track-video-types").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

        // missing value - bad request
        json = mapper.writeValueAsString(new TrackDVTypeUserUpdateDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1).build())
                .build());
        logger.info("testUpdateVideoTypesWrongParams testUpdateVideoTypes update object:{}", json);

        this.mockMvc.perform(
            post("/api/track/update-track-video-types").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
    }

    @Test
    @Order(14)
    void testUpdateVideoTypes() throws Exception {
        var result_before = mockMvc.perform(get("/api/track/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].dvType.id", Matchers.not(8)))
                .andExpect(jsonPath("$[1].dvType").doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testUpdateVideoTypes testUpdateVideoTypes before:{}", result_before);

        String json = mapper.writeValueAsString(new TrackDVTypeUserUpdateDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1).build())
                .withDVType(new IdNameDTOBuilder().withId(8).build())
                .build());

        logger.info("testUpdateVideoTypes update object:" + json);

        var result = this.mockMvc.perform(
                        post("/api/track/update-track-video-types").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$.rowsAffected", Matchers.equalTo(2)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testUpdateVideoTypes result:{}", result);

        var result_after = mockMvc.perform(get("/api/track/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].dvType.id", Matchers.equalTo(8)))
                .andExpect(jsonPath("$[1].dvType.id", Matchers.equalTo(8)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testUpdateVideoTypes after:{}", result_after);
    }

    @Test
    @Order(15)
    void testUpdateSelectedVideoTypes() throws Exception {
        var result_before = mockMvc.perform(get("/api/track/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].dvType.id", Matchers.equalTo(8)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testUpdateSelectedVideoTypes before:{}", result_before);

        String json = mapper.writeValueAsString(new TrackSelectedDVTypeUserUpdateDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1).build())
                .withTrackIds(List.of(3L))
                .withDVType(new IdNameDTOBuilder().withId(7).build())
                .build());

        logger.info("testUpdateSelectedVideoTypes update object:" + json);

        var result = this.mockMvc.perform(
                        post("/api/track/update-selected-track-video-types").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$.rowsAffected", Matchers.equalTo(1)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testUpdateSelectedVideoTypes result:{}", result);

        var result_after = mockMvc.perform(get("/api/track/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].dvType.id", Matchers.equalTo(8)))
                .andExpect(jsonPath("$[1].dvType.id", Matchers.equalTo(7)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testUpdateSelectedVideoTypes after:{}", result_after);
    }

    @Test
    @Order(20)
    void testGetTableByOptional() throws Exception {
        var resultNoArgs = mockMvc.perform(get("/api/track/table-by-optional"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].artifactType.id", Matchers.equalTo(202)))
                .andExpect(jsonPath("$[0].artifactType.name", Matchers.equalTo("Movies")))
                .andExpect(jsonPath("$[0].artifact.id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].artifact.title", Matchers.equalTo("Title 1")))
                .andExpect(jsonPath("$[0].num", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("34")))
                .andExpect(jsonPath("$[0].duration", Matchers.equalTo(685)))
                .andExpect(jsonPath("$[1].title", Matchers.equalTo("Music track title")))
                .andExpect(jsonPath("$[1].duration", Matchers.equalTo(5233)))
                .andExpect(jsonPath("$[1].artist.id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[1].artist.artistName", Matchers.equalTo("Jimmy")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.debug("testGetTableByOptional Get resultNoArgs:{}", resultNoArgs);
    }

    @Test
    @Order(21)
    void testUpdateTags() throws Exception {
        var trackTagsWrongArtifact = new ArtifactDTOBuilder()
                .withId(955L)
                .withTags(List.of("Warm", "Cold", "Wet"))
                .build();
        String jsonWrongArtifact = mapper.writeValueAsString(trackTagsWrongArtifact);

        this.mockMvc.perform(put("/api/track/update-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWrongArtifact)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        var trackTagsOk = new TrackDTOBuilder()
                .withId(1L)
                .withTags(List.of("Warm", "Cold", "Wet"))
                .build();
        String jsonOk = mapper.writeValueAsString(trackTagsOk);

        this.mockMvc.perform(put("/api/track/update-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonOk)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.tags", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.tags[0]", Matchers.is("Cold")))
                .andExpect(jsonPath("$.tags[1]", Matchers.is("Warm")))
                .andExpect(jsonPath("$.tags[2]", Matchers.is("Wet")))
        ;

        var trackDeleteOne = new TrackDTOBuilder()
                .withId(1L)
                .withTags(List.of("Wet", "Cold"))
                .build();
        String jsonDeleteOne = mapper.writeValueAsString(trackDeleteOne);

        this.mockMvc.perform(put("/api/track/update-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDeleteOne)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.tags", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.tags[0]", Matchers.is("Cold")))
                .andExpect(jsonPath("$.tags[1]", Matchers.is("Wet")))
        ;

        var result_2 = mockMvc.perform(get("/api/track/table")
                        .param("dvProductId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(11)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].artifact.id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].artifact.title", Matchers.equalTo("Title 1")))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("Track title")))
                .andExpect(jsonPath("$[0].tags").isArray())
                .andExpect(jsonPath("$[0].tags", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].tags[0]", Matchers.is("Cold")))
                .andExpect(jsonPath("$[0].tags[1]", Matchers.is("Wet")))
                .andReturn()
                .getResponse()
                .getContentAsString()
                ;
        logger.debug("testGetTableByProductId Get 2 result:{}", result_2);

        var trackDeleteAll = new TrackDTOBuilder()
                .withId(1L)
                .build();
        String jsonDeleteAll = mapper.writeValueAsString(trackDeleteAll);

        this.mockMvc.perform(put("/api/track/update-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDeleteAll)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags", Matchers.hasSize(0)))
        ;
    }

    @Test
    @Order(22)
    void testUpdateSelectedTags() throws Exception {
        var trackTagsWrongArtifact = new TrackSelectedTagsUserUpdateDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(854L).build())
                .withTrackIds(List.of(1L, 2L))
                .withTags(List.of("Warm", "Cold", "Wet"))
                .build();
        String jsonWrongArtifact = mapper.writeValueAsString(trackTagsWrongArtifact);

        this.mockMvc.perform(post("/api/track/update-selected-track-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWrongArtifact)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

      var trackTagsEmptyIds = new TrackSelectedTagsUserUpdateDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1L).build())
                .build();
        String jsonEmptyIds = mapper.writeValueAsString(trackTagsEmptyIds);

        this.mockMvc.perform(post("/api/track/update-selected-track-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEmptyIds)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


        var trackTagsOk = new TrackSelectedTagsUserUpdateDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1L).build())
                .withTrackIds(List.of(1L))
                .withTags(List.of("Violet", "Green"))
                .build();
        String jsonOk = mapper.writeValueAsString(trackTagsOk);

        this.mockMvc.perform(post("/api/track/update-selected-track-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonOk)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                        .andExpect(jsonPath("$.rowsAffected", Matchers.equalTo(1)))
        ;

        var result_1 = mockMvc.perform(get("/api/track/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("Track title")))
                .andExpect(jsonPath("$[0].tags").isArray())
                .andExpect(jsonPath("$[0].tags", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].tags[0]", Matchers.is("Green")))
                .andExpect(jsonPath("$[0].tags[1]", Matchers.is("Violet")))
                .andExpect(jsonPath("$[1].tags").isArray())
                .andExpect(jsonPath("$[1].tags", Matchers.hasSize(0)))
                .andReturn()
                .getResponse()
                .getContentAsString()
                ;
        logger.debug("testUpdateSelectedTags Get 1 result:{}", result_1);

        var trackRemoveTagsOk = new TrackSelectedTagsUserUpdateDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1L).build())
                .withTrackIds(List.of(1L))
                .withTags(List.of())
                .build();
        String jsonRemoveOk = mapper.writeValueAsString(trackRemoveTagsOk);

        this.mockMvc.perform(post("/api/track/update-selected-track-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRemoveOk)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$.rowsAffected", Matchers.equalTo(1)))
        ;

        var result_2 = mockMvc.perform(get("/api/track/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("Track title")))
                .andExpect(jsonPath("$[0].tags").isArray())
                .andExpect(jsonPath("$[0].tags", Matchers.hasSize(0)))
                .andExpect(jsonPath("$[1].tags").isArray())
                .andExpect(jsonPath("$[1].tags", Matchers.hasSize(0)))
                .andReturn()
                .getResponse()
                .getContentAsString()
                ;
        logger.debug("testUpdateSelectedTags Get 2 result:{}", result_2);

        var track3TagsOk = new TrackSelectedTagsUserUpdateDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1L).build())
                .withTrackIds(List.of(3L))
                .withTags(List.of("Fast"))
                .build();
        String json3Ok = mapper.writeValueAsString(track3TagsOk);

        this.mockMvc.perform(post("/api/track/update-selected-track-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json3Ok)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$.rowsAffected", Matchers.equalTo(1)))
        ;

        var result_3 = mockMvc.perform(get("/api/track/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("Track title")))
                .andExpect(jsonPath("$[0].tags").isArray())
                .andExpect(jsonPath("$[0].tags", Matchers.hasSize(0)))
                .andExpect(jsonPath("$[1].tags").isArray())
                .andExpect(jsonPath("$[1].tags", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[1].tags[0]", Matchers.is("Fast")))
                .andReturn()
                .getResponse()
                .getContentAsString()
                ;
        logger.debug("testUpdateSelectedTags Get 3 result:{}", result_3);
    }

}
