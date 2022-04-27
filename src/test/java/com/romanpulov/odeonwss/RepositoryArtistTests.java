package com.romanpulov.odeonwss;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositoryArtistTests {

    @Autowired
    EntityManager em;

    @Autowired
    ArtistRepository artistRepository;

    @BeforeEach
    @Transactional
    void beforeEach() {
        artistRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testAutoIncrement() throws Exception {
        List<Artist> artists = artistRepository.getAllByType("A");
        Assertions.assertEquals(0, artists.size());

        artistRepository.save(new Artist(null, "A", "Name 1"));

        artists = artistRepository.getAllByType("A");
        Assertions.assertEquals(1, artists.size());
        Assertions.assertEquals(1L, artists.get(0).getId());

        Artist artist = artistRepository.save(new Artist(null, "A", "Name 2"));
        artists = artistRepository.getAllByType("A");
        Assertions.assertEquals(2, artists.size());
        Assertions.assertEquals(2L, artist.getId());

        artistRepository.delete(artist);
        artists = artistRepository.getAllByType("A");
        Assertions.assertEquals(1, artists.size());
        artist = artistRepository.save(new Artist(null, "A", "Name 3"));
        Assertions.assertEquals(3L, artist.getId());
    }

    @Test
    @Order(2)
    void testAutoIncrement1() throws Exception {
        Assertions.assertEquals(0, artistRepository.getAllByType("A").size());
    }
}
