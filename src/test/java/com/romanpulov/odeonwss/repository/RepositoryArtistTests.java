package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.*;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.jdbc.Sql;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryArtistTests {

    @Autowired
    EntityManager em;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtistCategoryRepository artistCategoryRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    ArtistDetailRepository artistDetailRepository;

    @Autowired
    ArtistLyricsRepository artistLyricsRepository;

    @Test
    @Order(1)
    @Sql(value = {"/schema.sql", "/data.sql"})
    void testAutoIncrement() {
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
                .withArtifactType(artifactTypeRepository.getWithMP3())
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

        List<IdNameDTO> artists = artistRepository.getByTypeOrderByName(ArtistType.ARTIST);
        Assertions.assertEquals("zzz", artists.get(2).getName());
        Assertions.assertEquals("nnn", artists.get(1).getName());
        Assertions.assertEquals("aaa", artists.get(0).getName());
    }

    @Test
    @Order(5)
    void testAllFlatDTO() {
        var a1 = new EntityArtistBuilder()
                .withType(ArtistType.ARTIST)
                .withName("category Artist")
                .build();
        artistRepository.save(a1);


        var g1 = new EntityArtistCategoryBuilder()
                .withType(ArtistCategoryType.GENRE)
                .withName("G1")
                .withArtist(a1)
                .build();
        artistCategoryRepository.save(g1);

        var s1 = new EntityArtistCategoryBuilder()
                .withType(ArtistCategoryType.STYLE)
                .withName("S2")
                .withArtist(a1)
                .build();
        artistCategoryRepository.save(s1);

        var s2 = new EntityArtistCategoryBuilder()
                .withType(ArtistCategoryType.STYLE)
                .withName("S1")
                .withArtist(a1)
                .build();
        artistCategoryRepository.save(s2);

        var ad1 = new EntityArtistDetailBuilder()
                .withArtist(a1)
                .withBiography("Bio")
                .build();
        artistDetailRepository.save(ad1);

        var al1 = new EntityArtistLyricsBuilder()
                .withArtist(a1)
                .withTitle("Lyrics a1")
                .withText("A nice text")
                .build();
        artistLyricsRepository.save(al1);

        var flatDTOs = artistRepository.findAllFlatDTO();
        assertThat(flatDTOs.get(0).getArtistName()).isEqualTo("aaa");

        assertThat(flatDTOs.get(1).getArtistName()).isEqualTo("category Artist");
        assertThat(flatDTOs.get(1).getArtistType()).isEqualTo("A");
        assertThat(flatDTOs.get(1).getCategoryType()).isEqualTo("G");
        assertThat(flatDTOs.get(1).getCategoryName()).isEqualTo("G1");
        assertThat(flatDTOs.get(1).getDetailId()).isEqualTo(1L);
        assertThat(flatDTOs.get(1).getHasLyrics()).isEqualTo(1L);

        assertThat(flatDTOs.get(2).getArtistName()).isEqualTo("category Artist");
        assertThat(flatDTOs.get(2).getArtistType()).isEqualTo("A");
        assertThat(flatDTOs.get(2).getCategoryType()).isEqualTo("S");
        assertThat(flatDTOs.get(2).getCategoryName()).isEqualTo("S1");
        assertThat(flatDTOs.get(2).getDetailId()).isEqualTo(1L);
        assertThat(flatDTOs.get(2).getHasLyrics()).isEqualTo(1L);

        assertThat(flatDTOs.get(3).getArtistName()).isEqualTo("category Artist");
        assertThat(flatDTOs.get(3).getArtistType()).isEqualTo("A");
        assertThat(flatDTOs.get(3).getCategoryType()).isEqualTo("S");
        assertThat(flatDTOs.get(3).getCategoryName()).isEqualTo("S2");
        assertThat(flatDTOs.get(3).getDetailId()).isEqualTo(1L);
        assertThat(flatDTOs.get(3).getHasLyrics()).isEqualTo(1L);
    }

}
