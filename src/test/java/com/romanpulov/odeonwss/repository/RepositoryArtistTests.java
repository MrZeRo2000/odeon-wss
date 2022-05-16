package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryArtistTests {

    @Autowired
    EntityManager em;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Test
    @Order(1)
    @Sql(value = {"/schema.sql", "/data.sql"})
    void testAutoIncrement() throws Exception {
        List<Artist> artists = artistRepository.getAllByType("A");
        artistRepository.deleteAll(artists);

        artists = artistRepository.getAllByType("A");
        Assertions.assertEquals(0, artists.size());

        artistRepository.save(new EntityArtistBuilder().withType("A").withName("Name 1").build());

        artists = artistRepository.getAllByType("A");
        Assertions.assertEquals(1, artists.size());
        Assertions.assertEquals(1L, artists.get(0).getId());

        Artist artist = artistRepository.save(new EntityArtistBuilder().withType("A").withName("Name 2").build());
        artists = artistRepository.getAllByType("A");
        Assertions.assertEquals(2, artists.size());
        Assertions.assertEquals(2L, artist.getId());

        artistRepository.delete(artist);
        artists = artistRepository.getAllByType("A");
        Assertions.assertEquals(1, artists.size());
        artist = artistRepository.save(new EntityArtistBuilder().withType("A").withName("Name 3").build());
        Assertions.assertEquals(3L, artist.getId());
    }

    @Test
    @Order(2)
    void testAutoIncrement1() {
        Assertions.assertEquals(2, artistRepository.getAllByType("A").size());
        artistRepository.deleteAll();
        Assertions.assertEquals(0, artistRepository.getAllByType("A").size());

        Artist artist = new Artist(10L, "A", "Name 10");
        Artist savedArtist = artistRepository.save(artist);
        Assertions.assertEquals(4, savedArtist.getId());
    }

    @Test
    @Order(3)
    void testCascade() {
        Artist artist = artistRepository.findById(4L).orElseThrow();

        Artifact artifact = new EntityArtifactBuilder()
                .withArtifactType(ArtifactType.withMP3())
                .withArtist(artist)
                .withTitle("Artifact title")
                .withYear(1999L)
                .withDuration(1234L)
                .build();
        artifactRepository.save(artifact);

        // delete with child element throws
        Assertions.assertThrows(JpaSystemException.class, () -> artistRepository.delete(artist));

        // delete without child elements works
        artifactRepository.delete(artifact);
        artistRepository.delete(artist);
        Assertions.assertThrows(NoSuchElementException.class, () -> artistRepository.findById(4L).orElseThrow());
    }
}
