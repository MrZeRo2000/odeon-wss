package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistLyricsBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryArtistLyricsTests {
    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtistLyricsRepository artistLyricsRepository;

    @Test
    @Order(1)
    @Sql(value = {"/schema.sql", "/data.sql"})
    void testCRUD() throws Exception {
        Artist artist1 = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 1").build());

        artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist1).withTitle("Title 2").withText("Text 2").build());
        artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist1).withTitle("Title 1").withText("Text 1").build());

        List<ArtistLyrics> artistLyricsList = artistLyricsRepository.findAllByArtistOrderByTitle(artist1);
        Assertions.assertEquals(2, artistLyricsList.size());
        Assertions.assertEquals("Title 1", artistLyricsList.get(0).getTitle());
        Assertions.assertEquals("Title 2", artistLyricsList.get(1).getTitle());
        Assertions.assertEquals("Text 2", artistLyricsList.get(1).getText());
    }
}