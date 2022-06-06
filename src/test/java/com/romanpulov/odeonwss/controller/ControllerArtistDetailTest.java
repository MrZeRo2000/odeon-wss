package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistDetail;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entitybuilder.EntityArtistDetailBuilder;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ControllerArtistDetailTest {

    final static Logger logger = LoggerFactory.getLogger(ControllerArtistDetailTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistDetailRepository artistDetailRepository;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void shouldBeOk() throws Exception {
        Artist artist = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 1").build());
        ArtistDetail artistDetail = artistDetailRepository.save(new EntityArtistDetailBuilder().withArtist(artist).withBiography("My bio").build());

        this.mockMvc.perform(get("/api/artist-detail/999")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().string(Matchers.containsString("not found")));

        MvcResult mvcResult = this.mockMvc.perform(get("/api/artist-detail/" + artist.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.biography", Matchers.is("My bio")))
                .andReturn();
        logger.debug("Get result: " + mvcResult.getResponse().getContentAsString());

    }
}
