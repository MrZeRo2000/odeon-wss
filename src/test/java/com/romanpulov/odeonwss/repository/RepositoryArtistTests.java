package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.view.IdNameView;
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
    ArtistCategoryRepository artistCategoryRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Test
    @Order(1)
    @Sql(value = {"/schema.sql", "/data.sql"})
    void testAutoIncrement() throws Exception {
        List<Artist> artists = artistRepository.getAllByType(ArtistType.ARTIST);
        artistRepository.deleteAll(artists);

        artists = artistRepository.getAllByType(ArtistType.ARTIST);
        Assertions.assertEquals(0, artists.size());

        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 1").build());

        artists = artistRepository.getAllByType(ArtistType.ARTIST);
        Assertions.assertEquals(1, artists.size());
        Assertions.assertEquals(1L, artists.get(0).getId());

        Artist artist = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 2").build());
        artists = artistRepository.getAllByType(ArtistType.ARTIST);
        Assertions.assertEquals(2, artists.size());
        Assertions.assertEquals(2L, artist.getId());

        artistRepository.delete(artist);
        artists = artistRepository.getAllByType(ArtistType.ARTIST);
        Assertions.assertEquals(1, artists.size());
        artist = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 3").build());
        Assertions.assertEquals(3L, artist.getId());
    }

    @Test
    @Order(2)
    void testAutoIncrement1() {
        Assertions.assertEquals(2, artistRepository.getAllByType(ArtistType.ARTIST).size());
        artistRepository.deleteAll();
        Assertions.assertEquals(0, artistRepository.getAllByType(ArtistType.ARTIST).size());

        Artist artist = new Artist(10L, ArtistType.ARTIST, "Name 10");
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

    @Test
    @Order(4)
    void testAllIdName() {
        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("zzz").build());
        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("aaa").build());
        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("nnn").build());

        List<IdNameView> artists = artistRepository.getByTypeOrderByName(ArtistType.ARTIST);
        Assertions.assertEquals("zzz", artists.get(2).getName());
        Assertions.assertEquals("nnn", artists.get(1).getName());
        Assertions.assertEquals("aaa", artists.get(0).getName());
    }

}
