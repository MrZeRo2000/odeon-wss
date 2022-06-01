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
import org.junit.jupiter.api.Test;
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
public class ControllerArtistCategoryTest {

    final static Logger logger = LoggerFactory.getLogger(ControllerArtistCategoryTest.class);

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
    void shouldBeOk() throws Exception {
        Artist artist = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 1").build());
        ArtistDetail artistDetail = artistDetailRepository.save(new EntityArtistDetailBuilder().withArtist(artist).withBiography("My bio").build());
        artistCategoryRepository.save(new EntityArtistCategoryBuilder().withArtist(artist).withType(ArtistCategoryType.GENRE).withName("Rock").build());
        artistCategoryRepository.save(new EntityArtistCategoryBuilder().withArtist(artist).withType(ArtistCategoryType.STYLE).withName("Alternative Rock").build());
        artistCategoryRepository.save(new EntityArtistCategoryBuilder().withArtist(artist).withType(ArtistCategoryType.STYLE).withName("Grunge").build());

        MvcResult mvcResult = this.mockMvc.perform(get("/api/artist-category/all-with-artists")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].genre", Matchers.is("Rock")))
                .andExpect(jsonPath("$[0].styles[0]", Matchers.is("Alternative Rock")))
                .andExpect(jsonPath("$[0].styles[1]", Matchers.is("Grunge")))
                .andReturn();
        logger.debug("Result: " + mvcResult.getResponse().getContentAsString());
    }
}
