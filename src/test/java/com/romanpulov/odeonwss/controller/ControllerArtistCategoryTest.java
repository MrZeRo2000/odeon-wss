package com.romanpulov.odeonwss.controller;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ControllerArtistCategoryTest {

    final static Logger logger = LoggerFactory.getLogger(ControllerArtistCategoryTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldBeOk() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/artist-category/all-with-artists")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        logger.debug("Result: " + mvcResult.getResponse().getContentAsString());
    }

}
