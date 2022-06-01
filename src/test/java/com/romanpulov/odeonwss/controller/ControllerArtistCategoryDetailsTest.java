package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistDetail;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entitybuilder.EntityArtistCategoryBuilder;
import com.romanpulov.odeonwss.entitybuilder.EntityArtistDetailBuilder;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerArtistCategoryDetailsTest {

    final static Logger logger = LoggerFactory.getLogger(ControllerArtistCategoryDetailsTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistDetailRepository artistDetailRepository;

    @Autowired
    private ArtistCategoryRepository artistCategoryRepository;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(1)
    void getShouldBeOk() throws Exception {
        Artist artist = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 1").build());
        ArtistDetail artistDetail = artistDetailRepository.save(new EntityArtistDetailBuilder().withArtist(artist).withBiography("My bio").build());
        artistCategoryRepository.save(new EntityArtistCategoryBuilder().withArtist(artist).withType(ArtistCategoryType.GENRE).withName("Rock").build());
        artistCategoryRepository.save(new EntityArtistCategoryBuilder().withArtist(artist).withType(ArtistCategoryType.STYLE).withName("Grunge").build());
        artistCategoryRepository.save(new EntityArtistCategoryBuilder().withArtist(artist).withType(ArtistCategoryType.STYLE).withName("Alternative Rock").build());

        MvcResult mvcResult = this.mockMvc.perform(get("/api/artist-category-details/" + artist.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.artistType", Matchers.is("A")))
                .andExpect(jsonPath("$.artistName", Matchers.is("Name 1")))
                .andExpect(jsonPath("$.artistBiography", Matchers.is("My bio")))
                .andExpect(jsonPath("$.genre", Matchers.is("Rock")))
                .andExpect(jsonPath("$.styles[0]", Matchers.is("Alternative Rock")))
                .andExpect(jsonPath("$.styles[1]", Matchers.is("Grunge")))
                .andReturn();
        logger.debug("Result: " + mvcResult.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void shouldFailedNotFound() throws Exception {
        this.mockMvc.perform(get("/api/artist-category-details/111")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("not found")))
        ;
    }
}
