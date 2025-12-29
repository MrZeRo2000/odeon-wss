package com.romanpulov.odeonwss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.odeonwss.builder.dtobuilder.ArtistDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistCategoryBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistDetailBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistLyricsBuilder;
import com.romanpulov.odeonwss.dto.ArtistDTO;
import com.romanpulov.odeonwss.dto.ArtistDTOImpl;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistDetail;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtistCategoryRepository;
import com.romanpulov.odeonwss.repository.ArtistDetailRepository;
import com.romanpulov.odeonwss.repository.ArtistLyricsRepository;
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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
public class ControllerArtistTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerArtistTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistCategoryRepository artistCategoryRepository;

    @Autowired
    private ArtistDetailRepository artistDetailRepository;

    @Autowired
    private ArtistLyricsRepository artistLyricsRepository;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(1)
    void testGenerateTestData() {
        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name23").build());
        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name76").build());
        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name55").build());

        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.CLASSICS).withName("Classics001").build());
    }

    @Test
    @Order(2)
    void testValidateGetAllIdName() throws Exception {
        mockMvc.perform(get("/api/artist/artists/table-id-name").param("artistTypeCode", "A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].name", Matchers.containsString("Name23")))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$[1].name", Matchers.containsString("Name55")))
                .andExpect(jsonPath("$[2].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[2].name", Matchers.containsString("Name76")))
        ;
        mockMvc.perform(get("/api/artist/artists/table-id-name").param("artistTypeCode", "C"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(4)))
                .andExpect(jsonPath("$[0].name", Matchers.containsString("Classics001")))
        ;
    }

    @Test
    @Order(3)
    void testGetTable() throws Exception {
        var result = mockMvc.perform(get("/api/artist/artists/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(4)))
                //
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(4)))
                .andExpect(jsonPath("$[0].artistName", Matchers.equalTo("Classics001")))
                .andExpect(jsonPath("$[0].artistType", Matchers.equalTo("C")))
                .andExpect(jsonPath("$[0].styles", Matchers.hasSize(0)))
                .andExpect(jsonPath("$[0].hasLyrics", Matchers.equalTo(false)))
                //
                .andExpect(jsonPath("$[1]", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[1].artistName", Matchers.equalTo("Name23")))
                .andExpect(jsonPath("$[1].artistType", Matchers.equalTo("A")))
                .andExpect(jsonPath("$[1].styles", Matchers.hasSize(0)))
                .andExpect(jsonPath("$[1].hasLyrics", Matchers.equalTo(false)))
                //
                .andExpect(jsonPath("$[2]", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$[2].id", Matchers.equalTo(3)))
                .andExpect(jsonPath("$[2].artistName", Matchers.equalTo("Name55")))
                .andExpect(jsonPath("$[2].artistType", Matchers.equalTo("A")))
                .andExpect(jsonPath("$[2].styles", Matchers.hasSize(0)))
                .andExpect(jsonPath("$[2].hasLyrics", Matchers.equalTo(false)))
                //
                .andExpect(jsonPath("$[3]", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$[3].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[3].artistName", Matchers.equalTo("Name76")))
                .andExpect(jsonPath("$[3].artistType", Matchers.equalTo("A")))
                .andExpect(jsonPath("$[3].styles", Matchers.hasSize(0)))
                .andExpect(jsonPath("$[3].hasLyrics", Matchers.equalTo(false)))
                //
                .andReturn();
        logger.debug("Get result: " + result.getResponse().getContentAsString());
    }

    @Test
    @Order(4)
    void testGetTableWithCategories() throws Exception {
        var a1 = new EntityArtistBuilder()
                .withType(ArtistType.ARTIST)
                .withName("with Categories")
                .build();
        artistRepository.save(a1);


        var g1 = new EntityArtistCategoryBuilder()
                .withType(ArtistCategoryType.GENRE)
                .withName("Rock")
                .withArtist(a1)
                .build();
        artistCategoryRepository.save(g1);

        var s1 = new EntityArtistCategoryBuilder()
                .withType(ArtistCategoryType.STYLE)
                .withName("Heavy Metal")
                .withArtist(a1)
                .build();
        artistCategoryRepository.save(s1);

        var s2 = new EntityArtistCategoryBuilder()
                .withType(ArtistCategoryType.STYLE)
                .withName("Grunge")
                .withArtist(a1)
                .build();
        artistCategoryRepository.save(s2);

        var ad1 = new EntityArtistDetailBuilder()
                .withArtist(a1)
                .withBiography("Bio 1")
                .build();
        artistDetailRepository.save(ad1);

        var al1 = new EntityArtistLyricsBuilder()
                .withArtist(a1)
                .withTitle("t1")
                .withText("Txt 1")
                .build();
        artistLyricsRepository.save(al1);

        var result = mockMvc.perform(get("/api/artist/artists/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(5)))
                //
                .andExpect(jsonPath("$[4]", Matchers.aMapWithSize(7)))
                .andExpect(jsonPath("$[4].id", Matchers.equalTo(5)))
                .andExpect(jsonPath("$[4].artistName", Matchers.equalTo("with Categories")))
                .andExpect(jsonPath("$[4].artistType", Matchers.equalTo("A")))
                .andExpect(jsonPath("$[4].genre", Matchers.equalTo("Rock")))
                .andExpect(jsonPath("$[4].styles", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[4].styles[0]", Matchers.equalTo("Grunge")))
                .andExpect(jsonPath("$[4].styles[1]", Matchers.equalTo("Heavy Metal")))
                .andExpect(jsonPath("$[4].detailId", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[4].hasLyrics", Matchers.equalTo(true)))
                //
                .andReturn();
        logger.debug("Get result with categories: " + result.getResponse().getContentAsString());
    }

    //Migrated

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(11)
    void getShouldBeOk() throws Exception {
        Artist artist = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 1").build());
        ArtistDetail artistDetail = artistDetailRepository.save(new EntityArtistDetailBuilder().withArtist(artist).withBiography("My bio").build());
        artistCategoryRepository.save(new EntityArtistCategoryBuilder().withArtist(artist).withType(ArtistCategoryType.GENRE).withName("Rock").build());
        artistCategoryRepository.save(new EntityArtistCategoryBuilder().withArtist(artist).withType(ArtistCategoryType.STYLE).withName("Grunge").build());
        artistCategoryRepository.save(new EntityArtistCategoryBuilder().withArtist(artist).withType(ArtistCategoryType.STYLE).withName("Alternative Rock").build());

        MvcResult mvcResult = this.mockMvc.perform(get("/api/artist/" + artist.getId())
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
    @Order(12)
    void shouldFailedNotFound() throws Exception {
        this.mockMvc.perform(get("/api/artist/111")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("not found")))
        ;
    }

    @Test
    @Order(13)
    void postWithSameNameShouldFailConflict() throws Exception {
        ArtistDTO acd = new ArtistDTOBuilder()
                .withArtistName("Name 1")
                .withArtistType(ArtistType.ARTIST)
                .withArtistBiography("Bio 3")
                .withGenre("Rock")
                .build();

        String json = mapper.writeValueAsString(acd);

        this.mockMvc.perform(post("/api/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", Matchers.containsString("already exists")));
    }

    @Test
    @Order(14)
    void postWithDifferentNameShouldBeOk() throws Exception {
        ArtistDTO acd = new ArtistDTOBuilder()
                .withArtistName("Name 3")
                .withArtistType(ArtistType.ARTIST)
                .withArtistBiography("Bio 3")
                .withGenre("Rock")
                .build();

        String json = mapper.writeValueAsString(acd);

        this.mockMvc.perform(post("/api/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistName", Matchers.containsString("Name 3")));
    }

    @Test
    @Order(15)
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
    @Order(16)
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
    @Order(17)
    void putWithRemovedGenreAndStylesShouldBeOk() throws Exception {
        ArtistDTO acd = artistService.getById(2L);
        ArtistDTOImpl dto = ArtistDTOImpl.fromArtistDTO(acd);

        dto.setGenre(null);
        dto.setStyles(List.of());

        String json = mapper.writeValueAsString(dto);

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
    @Order(18)
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
    @Order(19)
    void deleteNotExistingArtistShouldFail() throws Exception {
        this.mockMvc.perform(delete("/api/artist/777"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("not found")));
    }

    @Test
    @Order(20)
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


