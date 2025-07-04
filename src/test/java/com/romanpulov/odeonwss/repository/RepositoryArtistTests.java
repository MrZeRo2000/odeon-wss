package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.*;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryArtistTests {

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
        assertThat(artists.size()).isEqualTo(0);

        artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 1").build());

        artists = artistRepository.getAllByType(ArtistType.ARTIST);
        assertThat(artists.size()).isEqualTo(1);
        assertThat(artists.get(0).getId()).isEqualTo(1L);

        Artist artist = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 2").build());
        artists = artistRepository.getAllByType(ArtistType.ARTIST);
        assertThat(artists.size()).isEqualTo(2);
        assertThat(artist.getId()).isEqualTo(2L);

        artistRepository.delete(artist);
        artists = artistRepository.getAllByType(ArtistType.ARTIST);
        assertThat(artists.size()).isEqualTo(1);

        artist = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 3").build());
        assertThat(artist.getId()).isEqualTo(3L);
    }

    @Test
    @Order(2)
    void testAutoIncrement1() {
        Assertions.assertEquals(2, artistRepository.getAllByType(ArtistType.ARTIST).size());
        artistRepository.deleteAll();
        Assertions.assertEquals(0, artistRepository.getAllByType(ArtistType.ARTIST).size());

        Artist artist = new Artist();
        artist.setType(ArtistType.ARTIST);
        artist.setName("Name 10");
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
        assertThat(flatDTOs.get(1).getArtistTypeCode()).isEqualTo("A");
        assertThat(flatDTOs.get(1).getCategoryTypeCode()).isEqualTo("G");
        assertThat(flatDTOs.get(1).getCategoryName()).isEqualTo("G1");
        assertThat(flatDTOs.get(1).getDetailId()).isEqualTo(1L);
        assertThat(flatDTOs.get(1).getHasLyrics()).isEqualTo(1L);

        assertThat(flatDTOs.get(2).getArtistName()).isEqualTo("category Artist");
        assertThat(flatDTOs.get(2).getArtistTypeCode()).isEqualTo("A");
        assertThat(flatDTOs.get(2).getCategoryTypeCode()).isEqualTo("S");
        assertThat(flatDTOs.get(2).getCategoryName()).isEqualTo("S1");
        assertThat(flatDTOs.get(2).getDetailId()).isEqualTo(1L);
        assertThat(flatDTOs.get(2).getHasLyrics()).isEqualTo(1L);

        assertThat(flatDTOs.get(3).getArtistName()).isEqualTo("category Artist");
        assertThat(flatDTOs.get(3).getArtistTypeCode()).isEqualTo("A");
        assertThat(flatDTOs.get(3).getCategoryTypeCode()).isEqualTo("S");
        assertThat(flatDTOs.get(3).getCategoryName()).isEqualTo("S2");
        assertThat(flatDTOs.get(3).getDetailId()).isEqualTo(1L);
        assertThat(flatDTOs.get(3).getHasLyrics()).isEqualTo(1L);
    }

    @Test
    @Order(6)
    void testArtistDetailCRUD() {
        var a = new EntityArtistBuilder()
                .withType(ArtistType.ARTIST)
                .withName("Artist Full Name")
                .build();

        var d = new EntityArtistDetailBuilder()
                .withArtist(a)
                .withBiography("Artist Full Bio")
                .build();

        a.setArtistDetails(List.of(d));

        artistRepository.save(a);
        artistDetailRepository.save(d);

        var d1 = new EntityArtistDetailBuilder()
                .withArtist(a)
                .withBiography("Artist Full Bio 2")
                .build();
        artistDetailRepository.delete(d);
        artistDetailRepository.save(d1);

        assertThat(artistRepository.findFlatDTOById(a.getId()).get(0).getDetailId())
                .isEqualTo(d1.getId());
    }
}
