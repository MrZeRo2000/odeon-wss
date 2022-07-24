package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtistLyricsEditDTO;
import com.romanpulov.odeonwss.dto.ArtistLyricsTableDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistLyricsBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
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
        Artist artist2 = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 2").build());

        artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist1).withTitle("Title 12").withText("Text 12").build());
        artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist1).withTitle("Title 11").withText("Text 11").build());

        artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist2).withTitle("Title 27").withText("Text 27").build());
        artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist2).withTitle("Title 23").withText("Text 23").build());
        artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist2).withTitle("Title 24").withText("Text 24").build());

        Assertions.assertThrows(JpaSystemException.class, () -> {
                artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist2).withTitle("Title 24").withText("Text 24").build());
        });

        List<ArtistLyrics> artistLyricsList1 = artistLyricsRepository.findAllByArtistOrderByTitle(artist1);
        Assertions.assertEquals(2, artistLyricsList1.size());
        Assertions.assertEquals("Title 11", artistLyricsList1.get(0).getTitle());
        Assertions.assertEquals("Title 12", artistLyricsList1.get(1).getTitle());
        Assertions.assertEquals("Text 12", artistLyricsList1.get(1).getText());

        List<ArtistLyrics> artistLyricsList2 = artistLyricsRepository.findAllByArtistOrderByTitle(artist2);
        Assertions.assertEquals(3, artistLyricsList2.size());
        Assertions.assertEquals("Text 23", artistLyricsList2.get(0).getText());
        Assertions.assertEquals("Text 27", artistLyricsList2.get(2).getText());
    }

    @Test
    @Order(2)
    void testTableDTO() {
        List<ArtistLyricsTableDTO> table = artistLyricsRepository.getArtistLyricsTableDTO();

        Assertions.assertEquals(5, table.size());
        Assertions.assertEquals("Name 1", table.get(0).getArtistName());
        Assertions.assertEquals("Title 11", table.get(0).getTitle());
        Assertions.assertEquals("Name 2", table.get(4).getArtistName());
        Assertions.assertEquals("Title 27", table.get(4).getTitle());
    }

    @Test
    @Order(2)
    void testEditDTO() {
        ArtistLyricsEditDTO editDTO = artistLyricsRepository.getArtistListEditById(1L).orElseThrow();
        Assertions.assertEquals(1, editDTO.getId());
        Assertions.assertEquals(1, editDTO.getArtistId());
        Assertions.assertEquals("Title 12", editDTO.getTitle());
        Assertions.assertEquals("Text 12", editDTO.getText());
    }

    @Test
    @Order(3)
    void testFindLyrics() {
        Assertions.assertEquals("Text 12", artistLyricsRepository.findArtistLyricsById(1L).orElseThrow().getText());
        Assertions.assertEquals("Text 24", artistLyricsRepository.findArtistLyricsById(5L).orElseThrow().getText());
    }
}