package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.*;
import com.romanpulov.odeonwss.dto.ArtifactFlatDTO;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.entity.Tag;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryArtifactTests {

    private static final Logger log = Logger.getLogger(RepositoryArtifactTests.class.getSimpleName());

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    TagRepository tagRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testDbState() {
        Assertions.assertFalse(artistRepository.findAll().iterator().hasNext());
        Assertions.assertTrue(artifactTypeRepository.findAll().iterator().hasNext());
        log.info("testDBState completed");
    }

    @Test
    @Order(2)
    void testInsertGet() {
        //ArtifactType
        ArtifactType artifactType = artifactTypeRepository.findAllById(List.of(101L)).iterator().next();
        Assertions.assertNotNull(artifactType);

        //Artist
        Artist artist = new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name1").build();
        artistRepository.save(artist);
        Assertions.assertNotNull(artist);
        Assertions.assertNotNull(artist.getId());

        Artist performerArtist = new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Performer Name1").build();
        artistRepository.save(performerArtist);
        Assertions.assertNotNull(performerArtist);
        Assertions.assertNotNull(performerArtist.getId());


        Artifact artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithMP3())
                .withArtist(artist)
                .withPerformerArtist(performerArtist)
                .withTitle("Title 1")
                .withYear(2000L)
                .withDuration(54334L)
                .withInsertDate(LocalDateTime.now().minusDays(1))
                .withMigrationId(321L)
                .build();

        artifactRepository.save(artifact);

        Assertions.assertNotNull(artifact);
        Assertions.assertNotNull(artifact.getId());
        Assertions.assertEquals(artist, artifact.getArtist());
        assert artifact.getPerformerArtist() != null;
        Assertions.assertEquals(performerArtist.getName(), artifact.getPerformerArtist().getName());
        Assertions.assertEquals(artifactType, artifactTypeRepository.getWithMP3());
        Assertions.assertEquals("Title 1", artifact.getTitle());
        Assertions.assertEquals(2000L, artifact.getYear());
        Assertions.assertEquals(54334L, artifact.getDuration());
        Assertions.assertEquals(321L, artifact.getMigrationId());

        log.info("Saved artifact summary:" + artifact);

        Assertions.assertEquals(1, artifactRepository.getArtifactsByArtist(artist).size());

        var foundArtifact = artifactRepository.findFirstByArtifactTypeAndArtistNameAndTitleAndYear(
                artifactTypeRepository.getWithMP3(),
                "Name1",
                "Title 1",
                2000L
        );
        assertThat(foundArtifact.isPresent()).isTrue();
    }

    @Test
    @Order(3)
    @Transactional
    void testGetManyToOne() {
        Artist artist = artistRepository.getAllByType(ArtistType.ARTIST).get(0);

        Artifact loadedArtifact = artifactRepository.getArtifactsByArtist(artist).get(0);
        Artist loadedArtist = loadedArtifact.getArtist();
        Assertions.assertNotNull(loadedArtist);
        Assertions.assertEquals(loadedArtist, artist);
    }

    @Test
    @Order(4)
    void testArtifactTable() {
        List<ArtifactFlatDTO> at = artifactRepository.findAllFlatDTOByArtistTypeAndArtifactTypeIds(
                ArtistType.ARTIST,
                List.of(artifactTypeRepository.getWithMP3().getId(), artifactTypeRepository.getWithLA().getId())
        );
        Assertions.assertEquals(1, at.size());

        Assertions.assertEquals(0,
            artifactRepository.findAllFlatDTOByArtistTypeAndArtifactTypeIds(
                    ArtistType.ARTIST,
                    List.of(artifactTypeRepository.getWithLA().getId())
            ).size()
        );

        Assertions.assertEquals(0,
                artifactRepository.findAllFlatDTOByArtistTypeAndArtifactTypeIds(
                        ArtistType.CLASSICS,
                        List.of(artifactTypeRepository.getWithMP3().getId())
                ).size()
        );
    }

    @Test
    @Order(5)
    void testArtifactsByArtistType() {
        Assertions.assertEquals(1, artifactRepository.getArtifactsByArtistType(ArtistType.ARTIST).size());
        Assertions.assertEquals(0, artifactRepository.getArtifactsByArtistType(ArtistType.CLASSICS).size());
        Assertions.assertEquals("Title 1", artifactRepository.getArtifactsByArtistType(ArtistType.ARTIST).get(0).getTitle());
    }

    @Test
    @Order(6)
    void testArtifactWithoutYear() {
        Artist artist = artistRepository.getAllByType(ArtistType.ARTIST).get(0);

        Artifact artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMusic())
                .withArtist(artist)
                .withTitle("Title No Year")
                .withDuration(77743L)
                .withInsertDate(LocalDateTime.now().minusDays(2))
                .withMigrationId(732L)
                .build();

        artifactRepository.save(artifact);

        Artifact getArtifact = artifactRepository.findFirstByArtifactTypeAndArtistAndTitleAndYear(
                artifactTypeRepository.getWithDVMusic(),
                artist,
                "Title No Year",
                null
        ).orElseThrow();

        Assertions.assertEquals("Title No Year", getArtifact.getTitle());
        Assertions.assertNull(getArtifact.getYear());
    }

    @Test
    @Order(7)
    void testArtifactWithoutArtist() {
        List<ArtifactFlatDTO> prevArtifactTable = artifactRepository
                .findAllFlatDTOByArtistTypeAndArtifactTypeIds(
                        ArtistType.ARTIST,
                        List.of(artifactTypeRepository.getWithDVMovies().getId()));
        assertThat(prevArtifactTable.size()).isEqualTo(0);
        Artifact artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
                .withTitle("Title No Artist")
                .withDuration(77743L)
                .withInsertDate(LocalDateTime.now().minusDays(5))
                .withMigrationId(732L)
                .build();

        artifactRepository.save(artifact);

        List<ArtifactFlatDTO> artifactTable = artifactRepository
                .findAllFlatDTOByArtistTypeAndArtifactTypeIds(
                        ArtistType.ARTIST,
                        List.of(artifactTypeRepository.getWithDVMovies().getId()));
        assertThat(artifactTable.size()).isEqualTo(1);

        Artifact savedArtifact = artifactRepository.findById(artifact.getId()).orElseThrow();
        assertThat(savedArtifact).isNotNull();
    }

    @Test
    @Order(8)
    void testArtifactsByArtifactType() {
        assertThat(artifactRepository.findAllArtifactsByArtifactType(artifactTypeRepository.getWithLA()).size())
                .isEqualTo(0L);
        assertThat(artifactRepository.findAllArtifactsByArtifactType(artifactTypeRepository.getWithDVMusic()).size())
                .isEqualTo(1L);
        assertThat(artifactRepository.findAllArtifactsByArtifactType(artifactTypeRepository.getWithDVMovies()).size())
                .isEqualTo(1L);

        var moviesArtifacts = artifactRepository.findAllArtifactsByArtifactType(artifactTypeRepository.getWithDVMovies());
        assertThat(moviesArtifacts.size()).isEqualTo(1);
        assertThat(moviesArtifacts.get(0).getId()).isEqualTo(3L);
        assertThat(moviesArtifacts.get(0).getTitle()).isEqualTo("Title No Artist");
    }

    @Test
    @Order(10)
    void testCascade() {
        Artifact artifact = artifactRepository.findAll().iterator().next();
        Assertions.assertNotNull(artifact);

        Track track = new EntityTrackBuilder()
                .withArtifact(artifact)
                .withTitle("Artifact title")
                .withDiskNum(1L)
                .withNum(1L)
                .withDuration(12345L)
                .build();

        trackRepository.save(track);

        track = new EntityTrackBuilder()
                .withArtifact(artifact)
                .withTitle("Artifact title 2")
                .withDiskNum(1L)
                .withNum(3L)
                .withDuration(123458L)
                .build();

        trackRepository.save(track);

        Assertions.assertEquals(2, trackRepository.findAllByArtifact(artifact).size());

        //insert media file
        Assertions.assertEquals(0, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());
        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withFormat("mp3")
                .withName("File Name.mp3")
                .withDuration(123456L)
                .withSize(888888L)
                .build();
        mediaFileRepository.save(mediaFile);
        Assertions.assertEquals(1, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());

        artifactRepository.delete(artifact);

        //tracks deleted
        Assertions.assertEquals(0, trackRepository.findAllByArtifact(artifact).size());

        //media files deleted
        Assertions.assertEquals(0, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());
    }

    @Test
    @Order(11)
    void testArtifactsWithoutTracks() {
        var withoutTracks = artifactRepository.getAllByArtifactTypeWithoutTracks(artifactTypeRepository.getWithDVMovies());
        assertThat(withoutTracks.size()).isEqualTo(1);

        var track = new EntityTrackBuilder()
                .withArtifact(withoutTracks.get(0))
                .withTitle("Track title")
                .withDiskNum(1L)
                .withNum(1L)
                .withDuration(123458L)
                .build();

        trackRepository.save(track);

        var withTracks = artifactRepository.getAllByArtifactTypeWithoutTracks(artifactTypeRepository.getWithDVMovies());
        assertThat(withTracks.size()).isEqualTo(0);
    }

    @Test
    @Order(12)
    void testArtifactsWithNoMonotonicallyIncreasingTrackNumbers() {
        var noNumbers = artifactRepository
                .getArtifactsWithNoMonotonicallyIncreasingTrackNumbers(
                        List.of(ArtistType.ARTIST.getCode()),
                        List.of(artifactTypeRepository.getWithDVMovies().getId()));
        assertThat(noNumbers.size()).isEqualTo(0);

        var artifact = trackRepository.getTracksByArtifactType(artifactTypeRepository.getWithDVMovies()).get(0).getArtifact();
        assertThat(artifact).isNotNull();

        var track = new EntityTrackBuilder()
                .withArtifact(artifact)
                .withTitle("Track title 2")
                .withDiskNum(1L)
                .withNum(3L)
                .withDuration(555L)
                .build();
        trackRepository.save(track);

        var hasNumbers = artifactRepository
                .getArtifactsWithNoMonotonicallyIncreasingTrackNumbers(
                        null,
                        List.of(artifactTypeRepository.getWithDVMovies().getId()));
        assertThat(hasNumbers.size()).isEqualTo(1);
        assertThat(hasNumbers.get(0).getId()).isNotNull();
        assertThat(hasNumbers.get(0).getTitle()).isEqualTo("Title No Artist");

        assertThat(artifactRepository
                .getArtifactsWithNoMonotonicallyIncreasingTrackNumbers(
                        List.of(ArtistType.ARTIST.getCode()),
                        List.of(artifactTypeRepository.getWithDVMusic().getId()))
                .size()).isEqualTo(0);
    }

    @Test
    @Order(12)
    void testArtifactTags() {
        var artifact = artifactRepository.save(
            new EntityArtifactBuilder()
                    .withArtifactType(artifactTypeRepository.getWithDVMovies())
                    .withTitle("Title with tags")
                    .withDuration(5000L)
                    .build()
        );

        var tagRed = tagRepository.save(
            new EntityTagBuilder()
                    .withName("Red")
                    .build()
        );

        var tagGreen = tagRepository.save(
            new EntityTagBuilder()
                    .withName("Green")
                    .build()
        );
        artifact.setTags(Set.of(tagGreen, tagRed));
        artifactRepository.save(artifact);

        var atg = artifactRepository.findAllFlatDTOTagsByArtifactId(artifact.getId());
        assertThat(atg.size()).isEqualTo(2);
        assertThat(atg.get(0).getTagName()).isEqualTo("Green");
        assertThat(atg.get(1).getTagName()).isEqualTo("Red");

        var att = artifactRepository.findAllFlatDTOByArtistTypeAndArtifactTypeIds(
                ArtistType.ARTIST,
                List.of(artifactTypeRepository.getWithDVMovies().getId()))
                .stream()
                .filter(a -> a.getId().equals(artifact.getId()))
                .toList();
        assertThat(att).isNotNull();
        assertThat(att.size()).isEqualTo(2);
        assertThat(att.get(0).getTitle()).isEqualTo("Title with tags");
        assertThat(att.get(1).getTitle()).isEqualTo("Title with tags");

        assertThat(att.get(0).getTagName()).isEqualTo(tagGreen.getName());
        assertThat(att.get(1).getTagName()).isEqualTo(tagRed.getName());

        artifact.setTags(Set.of(tagGreen));
        artifactRepository.save(artifact);

        var atg2 = artifactRepository.findAllFlatDTOTagsByArtifactId(artifact.getId());
        assertThat(atg2.size()).isEqualTo(1);

        artifact.setTags(Set.of(tagGreen, tagRed));
        artifactRepository.save(artifact);

        var atg3 = artifactRepository.findAllFlatDTOTagsByArtifactId(artifact.getId());
        assertThat(atg3.size()).isEqualTo(2);
    }

    @Test
    @Order(13)
    @Disabled
    void testTags() {
        List<Artifact> artifacts = artifactRepository.findAll();
        assertThat(artifacts.size()).isGreaterThan(1);

        Artifact a1 = artifacts.get(0);
        Artifact a2 = artifacts.get(1);

        Tag tag1 = new EntityTagBuilder().withName("tag1").build();
        tagRepository.save(tag1);

        Tag tag2 = new EntityTagBuilder().withName("tag2").build();
        tagRepository.save(tag2);

        a1.setTags(Set.of(tag1));
        a2.setTags(Set.of(tag1, tag2));

        artifactRepository.save(a1);
        artifactRepository.save(a2);

        Artifact a21 = artifactRepository.findById(a1.getId()).orElseThrow();
        a21.setTags(Set.of());
        artifactRepository.save(a21);
    }

    @Test
    @Order(14)
    void testFindAllByOptional() {
        var noArgs = artifactRepository.findAllFlatDTOByOptional(
                0L, null,
                0L, null
        );
        assertThat(noArgs.size()).isEqualTo(4);

        assertThat(noArgs.get(0).getArtifactTypeId()).isEqualTo(artifactTypeRepository.getWithDVMovies().getId());
        assertThat(noArgs.get(0).getArtistId()).isNull();
        assertThat(noArgs.get(0).getTitle()).isEqualTo("Title No Artist");
        assertThat(noArgs.get(0).getDuration()).isEqualTo(77743L);

        var byArtist = artifactRepository.findAllFlatDTOByOptional(
                0L,null,
                2L, List.of(1L, 15L)
        );
        assertThat(byArtist.size()).isEqualTo(1);

        var byArtifactType = artifactRepository.findAllFlatDTOByOptional(
                1L, List.of(artifactTypeRepository.getWithDVMovies().getId()),
                0L, null
        );
        assertThat(byArtifactType.size()).isEqualTo(3); //with tags

        var byArtistAndArtifactType = artifactRepository.findAllFlatDTOByOptional(
                1L,
                List.of(artifactTypeRepository.getWithDVMusic().getId()),
                1L, List.of(1L)
        );
        assertThat(byArtistAndArtifactType.size()).isEqualTo(1);

        var byAllArtifactTypes = artifactRepository.findAllFlatDTOByOptional(
                2L,
                List.of(artifactTypeRepository.getWithDVMovies().getId(), artifactTypeRepository.getWithDVMusic().getId()),
                0L,
                null
        );
        assertThat(byAllArtifactTypes.size()).isEqualTo(4);
    }
}
