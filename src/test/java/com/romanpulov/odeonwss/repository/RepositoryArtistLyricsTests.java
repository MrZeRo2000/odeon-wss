package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistLyricsBuilder;
import com.romanpulov.odeonwss.dto.ArtistLyricsDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    void testCRUD() {
        Artist artist1 = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 1").build());
        Artist artist2 = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 2").build());

        artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist1).withTitle("Title 12").withText("Text 12").build());
        artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist1).withTitle("Title 11").withText("Text 11").build());

        artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist2).withTitle("Title 27").withText("Text 27").build());
        artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist2).withTitle("Title 23").withText("Text 23").build());
        artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist2).withTitle("Title 24").withText("Text 24").build());

        Assertions.assertThrows(JpaSystemException.class,
                () -> artistLyricsRepository.save(new EntityArtistLyricsBuilder().withArtist(artist2).withTitle("Title 24").withText("Text 24").build()));

        var artistLyricsList1 = artistLyricsRepository.findAllDTOByArtistId(artist1.getId());
        Assertions.assertEquals(2, artistLyricsList1.size());
        Assertions.assertEquals("Title 11", artistLyricsList1.get(0).getTitle());
        Assertions.assertEquals("Title 12", artistLyricsList1.get(1).getTitle());

        var artistLyricsList2 = artistLyricsRepository.findAllDTOByArtistId(artist2.getId());
        Assertions.assertEquals(3, artistLyricsList2.size());
        Assertions.assertEquals("Title 23", artistLyricsList2.get(0).getTitle());
        Assertions.assertEquals("Title 27", artistLyricsList2.get(2).getTitle());
    }

    @Test
    @Order(2)
    void testFindById() {
        var dto = artistLyricsRepository.findDTOById(4L).orElseThrow();
        assertThat(dto.getId()).isEqualTo(4L);
        assertThat(dto.getArtistId()).isEqualTo(2L);
        assertThat(dto.getTitle()).isEqualTo("Title 23");
        assertThat(dto.getText()).isEqualTo("Text 23");
    }

    @Test
    @Order(2)
    void testTableDTO() {
        List<ArtistLyricsDTO> table = artistLyricsRepository.findAllDTO();

        Assertions.assertEquals(5, table.size());
        Assertions.assertEquals("Name 1", table.get(0).getArtistName());
        Assertions.assertEquals("Title 11", table.get(0).getTitle());
        Assertions.assertEquals("Name 2", table.get(4).getArtistName());
        Assertions.assertEquals("Title 27", table.get(4).getTitle());
    }

    @Test
    @Order(2)
    void testEditDTO() {
        ArtistLyricsDTO editDTO = artistLyricsRepository.findDTOById(1L).orElseThrow();
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

    @Test
    @Order(4)
    void testFindDistinctArtist() {
        var a = artistLyricsRepository.findDistinctArtistId();
        assertThat(a).isEqualTo(Set.of(1L, 2L));
    }
}