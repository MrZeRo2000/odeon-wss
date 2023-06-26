package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVProductBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityTrackBuilder;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.entity.DVProduct;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.repository.*;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private DVOriginRepository dvOriginRepository;

    @Autowired
    private DVProductRepository dvProductRepository;

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

        DVProduct dvProduct = dvProductRepository.findById(2L).orElseThrow();

        Track track = new EntityTrackBuilder()
                .withArtifact(artifact)
                .withTitle("Track title")
                .withDiskNum(1L)
                .withNum(8L)
                .withDuration(6665L)
                .build();
        track.getDvProducts().add(dvProduct);

        trackRepository.save(track);
        assertThat(track.getId()).isGreaterThan(0);

        logger.debug("Data generated");
    }

    @Test
    @Order(2)
    void testGetTableByProductId() throws Exception {
        var result_1 = mockMvc.perform(get("/api/track/table")
                        .param("dvProductId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();
        logger.debug("Get 1 result:" + result_1.getResponse().getContentAsString());

        var result_2 = mockMvc.perform(get("/api/track/table")
                        .param("dvProductId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("Track title")))
                .andExpect(jsonPath("$[0].num", Matchers.equalTo(8)))
                .andExpect(jsonPath("$[0].dvProduct.title", Matchers.equalTo("Big")))
                .andReturn()
                ;
        logger.debug("Get 2 result:" + result_2.getResponse().getContentAsString());
    }
}
