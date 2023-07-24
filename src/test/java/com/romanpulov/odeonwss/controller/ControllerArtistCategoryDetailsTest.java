package com.romanpulov.odeonwss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.odeonwss.builder.dtobuilder.ArtistCategoriesDetailDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistCategoryBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistDetailBuilder;
import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.dto.ArtistDTO;
import com.romanpulov.odeonwss.dto.ArtistDTOImpl;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistDetail;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtistCategoryRepository;
import com.romanpulov.odeonwss.repository.ArtistDetailRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.ArtistService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ObjectMapper mapper;

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

    @Test
    @Order(3)
    void postWithSameNameShouldFailConflict() throws Exception {
        ArtistCategoriesDetailDTO acd = new ArtistCategoriesDetailDTOBuilder()
                .withArtistName("Name 1")
                .withArtistType(ArtistType.ARTIST)
                .withArtistBiography("Bio 3")
                .withGenre("Rock")
                .build();

        String json = mapper.writeValueAsString(acd);

        this.mockMvc.perform(post("/api/artist-category-details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", Matchers.containsString("already exists")));
    }

    @Test
    @Order(4)
    void postWithDifferentNameShouldBeOk() throws Exception {
        ArtistCategoriesDetailDTO acd = new ArtistCategoriesDetailDTOBuilder()
                .withArtistName("Name 3")
                .withArtistType(ArtistType.ARTIST)
                .withArtistBiography("Bio 3")
                .withGenre("Rock")
                .build();

        String json = mapper.writeValueAsString(acd);

        this.mockMvc.perform(post("/api/artist-category-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistName", Matchers.containsString("Name 3")));
    }

    @Test
    @Order(5)
    void putWithNewBiographyShouldBeOk() throws Exception {
        ArtistDTO acd = artistService.getById(2L);
        ArtistDTOImpl dto = ArtistDTOImpl.fromArtistDTO(acd);
        dto.setArtistBiography("Bio 3 changed");

        String json = mapper.writeValueAsString(dto);

        this.mockMvc.perform(put("/api/artist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistBiography", Matchers.containsString("Bio 3 changed")));
    }

    @Test
    @Order(6)
    void putWithGenreAndStylesShouldBeOk() throws Exception {
        ArtistDTO acd = artistService.getById(2L);
        ArtistDTOImpl dto = ArtistDTOImpl.fromArtistDTO(acd);

        dto.setGenre("Rock");
        dto.setStyles(List.of("Rap", "Alternative Rock"));

        String json = mapper.writeValueAsString(dto);

        this.mockMvc.perform(put("/api/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genre", Matchers.containsString("Rock")))
                .andExpect(jsonPath("$.styles[0]", Matchers.containsString("Alternative Rock")))
                .andExpect(jsonPath("$.styles[1]", Matchers.containsString("Rap")))
        ;
    }

    @Test
    @Order(7)
    void putWithRemovedGenreAndStylesShouldBeOk() throws Exception {
        ArtistDTO acd = artistService.getById(2L);
        ArtistDTOImpl dto = ArtistDTOImpl.fromArtistDTO(acd);

        dto.setGenre(null);
        dto.setStyles(List.of());

        String json = mapper.writeValueAsString(acd);

        this.mockMvc.perform(put("/api/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.styles", Matchers.empty()))
        ;
    }

    @Test
    @Order(8)
    void putNotExistingArtistShouldFail() throws Exception {
        ArtistDTO acd = artistService.getById(2L);
        ArtistDTOImpl dto = ArtistDTOImpl.fromArtistDTO(acd);
        dto.setId(777L);

        String json = mapper.writeValueAsString(dto);

        this.mockMvc.perform(put("/api/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("not found")));
    }

    @Test
    @Order(9)
    void deleteNotExistingArtistShouldFail() throws Exception {
        this.mockMvc.perform(delete("/api/artist/777"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("not found")));
    }

    @Test
    @Order(10)
    void deleteExistingArtistShouldBeOk() throws Exception {
        this.mockMvc.perform(get("/api/artist/2")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        this.mockMvc.perform(delete("/api/artist/2"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/api/artist/2")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("not found")));
    }
}
