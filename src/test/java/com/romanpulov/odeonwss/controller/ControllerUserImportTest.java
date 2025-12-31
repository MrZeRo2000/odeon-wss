package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.builder.dtobuilder.*;
import com.romanpulov.odeonwss.builder.entitybuilder.*;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.repository.*;
import org.assertj.core.util.Lists;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerUserImportTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerUserImportTest.class);

    final List<String> PRODUCT_NAMES = List.of(
            "Street",
            "Racer"
    );

    final List<String> ARTISTS = List.of(
            "Fergie",
            "Various Artists",
            "Tool"
    );

    @Autowired
    private JsonMapper mapper;

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

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    ArtistRepository artistRepository;

    private ArtifactType getArtifactType() {
        return artifactTypeRepository.getWithDVAnimation();
    }

    private ArtifactType getMusicArtifactType() {
        return artifactTypeRepository.getWithDVMusic();
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

        ARTISTS.forEach(s -> artistRepository.save(
                new EntityArtistBuilder().withType(ArtistType.ARTIST).withName(s).build()
        ));

        dvCategoryRepository.saveAll(List.of(
                (new EntityDVCategoryBuilder()).withName("Cat 01").build(),
                (new EntityDVCategoryBuilder()).withName("Cat 02").build()
        ));

        var artifactOne = new EntityArtifactBuilder()
                .withArtifactType(getArtifactType())
                .withTitle("Number One")
                .withDuration(2500L)
                .build();
        artifactRepository.save(artifactOne);

        var mediaFileFirst = new EntityMediaFileBuilder()
                .withArtifact(artifactOne)
                .withDuration(2000L)
                .withName("Number One File Name First")
                .withBitrate(1000L)
                .withFormat("MKV")
                .withSize(524456L)
                .build();
        mediaFileRepository.save(mediaFileFirst);

        var artist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, "Various Artists").orElseThrow();
        var artifactMusic = new EntityArtifactBuilder()
                .withArtifactType(getMusicArtifactType())
                .withArtist(artist)
                .withTitle("Music Title")
                .withDuration(54564L)
                .build();
        artifactRepository.save(artifactMusic);

        var mediaFileMusic = new EntityMediaFileBuilder()
                .withArtifact(artifactMusic)
                .withDuration(54564L)
                .withName("Music File Name")
                .withBitrate(2500L)
                .withFormat("MKV")
                .withSize(72834L)
                .build();
        mediaFileRepository.save(mediaFileMusic);
    }

    @Test
    @Order(2)
    void testDVProductAnalyzeNoArtifactTypeShouldFail() throws Exception {
        mockMvc.perform(post("/api/user-import/dvproduct/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void testDVProductAnalyzeNoDvOriginShouldFail() throws Exception {
        var dataNoOrigin = (new DVProductUserImportDTOBuilder())
                .withArtifactTypeId(getArtifactType().getId())
                .build();

        String json = mapper.writeValueAsString(dataNoOrigin);
        logger.debug("no origin json:" + json);

        mockMvc.perform(post("/api/user-import/dvproduct/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void testDVProductAnalyzeWrongCategoriesShouldFail() throws Exception {
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

        mockMvc.perform(post("/api/user-import/dvproduct/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    void testDVProductExecuteInsertOneShouldBeOk() throws Exception {
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

        var result = mockMvc.perform(post("/api/user-import/dvproduct/execute")
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

    @Test
    @Order(11)
    void testTrackExecuteShouldBeOk() throws Exception {
        var chapters = new String[]{
                "CHAPTER01=00:00:00.000",
                "CHAPTER01NAME=Chapter 01",
                "CHAPTER02=00:06:28.160",
                "CHAPTER02NAME=Chapter 02",
                "CHAPTER03=00:12:26.160",
                "CHAPTER03NAME=Chapter 03",
                "CHAPTER04=00:19:30.160",
                "CHAPTER04NAME=Chapter 04"
        };

        var data = new TrackUserImportDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1L).build())
                .withDVType(new IdNameDTOBuilder().withId(2L).build())
                .withMediaFile(new MediaFileDTOBuilder().withId(1L).build())
                .withTitles(List.of("Street", "Racer", "Magenta"))
                .withChapters(Lists.list(chapters))
                .build();

        String json = mapper.writeValueAsString(data);
        logger.debug("ok data json:" + json);

        var result = mockMvc.perform(post("/api/user-import/track/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rowsInserted").isArray())
                .andExpect(jsonPath("$.rowsInserted", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.rowsInserted[0]", Matchers.equalTo("Street")))
                .andExpect(jsonPath("$.rowsInserted[1]", Matchers.equalTo("Racer")))
                .andExpect(jsonPath("$.rowsInserted[2]", Matchers.equalTo("Magenta")))
                .andExpect(jsonPath("$.rowsUpdated").isArray())
                .andExpect(jsonPath("$.rowsUpdated", Matchers.hasSize(0)))
                .andReturn();
        logger.debug("Post result:" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(11)
    void testTrackExecuteTitlesMismatchShouldBeOk() throws Exception {
        var chapters = new String[]{
                "CHAPTER01=00:00:00.000",
                "CHAPTER01NAME=Chapter 01",
                "CHAPTER02=00:06:28.160",
                "CHAPTER02NAME=Chapter 02",
                "CHAPTER03=00:12:26.160",
                "CHAPTER03NAME=Chapter 03",
                "CHAPTER04=00:19:30.160",
                "CHAPTER04NAME=Chapter 04"
        };

        var data = new TrackUserImportDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1L).build())
                .withDVType(new IdNameDTOBuilder().withId(2L).build())
                .withMediaFile(new MediaFileDTOBuilder().withId(1L).build())
                .withTitles(List.of("Slow", "Deep"))
                .withChapters(Lists.list(chapters))
                .build();

        String json = mapper.writeValueAsString(data);
        logger.debug("ok data json:" + json);

        var result = mockMvc.perform(post("/api/user-import/track/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.equalTo("Wrong parameter Chapters value: Titles size:2 and chapters duration size:3 mismatch")))
                .andReturn();
        logger.debug("Post result:" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(12)
    void testTrackExecuteMusicShouldBeOk() throws Exception {
        var data = new TrackUserImportDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(2L).build())
                .withDVType(new IdNameDTOBuilder().withId(2L).build())
                .withMediaFile(new MediaFileDTOBuilder().withId(2L).build())
                .withTitles(List.of("Slow", "Deep"))
                .withArtists(List.of("Tool", "Fergie"))
                .build();

        String json = mapper.writeValueAsString(data);
        logger.debug("ok data json:" + json);

        var result = mockMvc.perform(post("/api/user-import/track/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rowsInserted").isArray())
                .andExpect(jsonPath("$.rowsInserted", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.rowsInserted[0]", Matchers.equalTo("Slow")))
                .andExpect(jsonPath("$.rowsInserted[1]", Matchers.equalTo("Deep")))
                .andReturn();
        logger.debug("Post result:" + result.getResponse().getContentAsString());

    }
}