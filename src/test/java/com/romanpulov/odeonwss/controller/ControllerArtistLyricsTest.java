package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.builder.dtobuilder.ArtistLyricsDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.ArtistLyricsService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
public class ControllerArtistLyricsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistLyricsService artistLyricsService;

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

        Artist artist2 = artistRepository.save(
                new EntityArtistBuilder()
                        .withType(ArtistType.ARTIST)
                        .withName("Artist 2")
                        .build()
        );

        artistLyricsService.insert(
                new ArtistLyricsDTOBuilder()
                        .withArtistId(artist1.getId())
                        .withTitle("Title 2")
                        .withText("Text 2")
                        .build()
        );

        artistLyricsService.insert(
                new ArtistLyricsDTOBuilder()
                        .withArtistId(artist1.getId())
                        .withTitle("Title 1")
                        .withText("Text 1")
                        .build()
        );

        artistLyricsService.insert(
                new ArtistLyricsDTOBuilder()
                        .withArtistId(artist2.getId())
                        .withTitle("Title 200")
                        .withText("Text 200")
                        .build()
        );
    }

    @Test
    @Order(2)
    void testGetTable() throws Exception {
        this.mockMvc.perform(get("/api/artist-lyrics/table")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].artistName", Matchers.is("Artist 1")))
                .andExpect(jsonPath("$[0].title", Matchers.is("Title 1")))
                .andExpect(jsonPath("$[0].text").doesNotExist())
                .andExpect(jsonPath("$[2].artistName", Matchers.is("Artist 2")))
                .andExpect(jsonPath("$[2].title", Matchers.is("Title 200")))
        ;
    }

    @Test
    @Order(2)
    void testGetTableById() throws Exception {
        this.mockMvc.perform(get("/api/artist-lyrics/table/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].artistName", Matchers.is("Artist 1")))
                .andExpect(jsonPath("$[0].title", Matchers.is("Title 1")))
                .andExpect(jsonPath("$[1].artistName", Matchers.is("Artist 1")))
                .andExpect(jsonPath("$[1].title", Matchers.is("Title 2")))
        ;

        this.mockMvc.perform(get("/api/artist-lyrics/table/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].artistName", Matchers.is("Artist 2")))
                .andExpect(jsonPath("$[0].title", Matchers.is("Title 200")))
        ;

        this.mockMvc.perform(get("/api/artist-lyrics/table/3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(0)))
        ;
    }

    @Test
    @Order(2)
    void testGetText() throws Exception {
        this.mockMvc.perform(get("/api/artist-lyrics/text/555")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(get("/api/artist-lyrics/text/3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", Matchers.is("Text 200")));
    }
}
