package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityMediaFileBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityTrackBuilder;
import com.romanpulov.odeonwss.dto.ArtifactTableDTO;
import com.romanpulov.odeonwss.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
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
        List<ArtifactTableDTO> at = artifactRepository.getArtifactTableByArtistTypeAndArtifactTypeIds(
                ArtistType.ARTIST,
                List.of(artifactTypeRepository.getWithMP3().getId(), artifactTypeRepository.getWithLA().getId())
        );
        Assertions.assertEquals(1, at.size());

        Assertions.assertEquals(0,
            artifactRepository.getArtifactTableByArtistTypeAndArtifactTypeIds(
                    ArtistType.ARTIST,
                    List.of(artifactTypeRepository.getWithLA().getId())
            ).size()
        );

        Assertions.assertEquals(0,
                artifactRepository.getArtifactTableByArtistTypeAndArtifactTypeIds(
                        ArtistType.CLASSICS,
                        List.of(artifactTypeRepository.getWithMP3().getId())
                ).size()
        );
    }

    @Test
    @Order(5)
    void testArtifactsByType() {
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
        List<ArtifactTableDTO> prevArtifactTable = artifactRepository
                .getArtifactTableByArtistTypeAndArtifactTypeIds(
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

        List<ArtifactTableDTO> artifactTable = artifactRepository
                .getArtifactTableByArtistTypeAndArtifactTypeIds(
                        ArtistType.ARTIST,
                        List.of(artifactTypeRepository.getWithDVMovies().getId()));
        assertThat(artifactTable.size()).isEqualTo(1);

        Artifact savedArtifact = artifactRepository.findById(artifact.getId()).orElseThrow();
        assertThat(savedArtifact).isNotNull();
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
                        artifactTypeRepository.getWithDVMovies().getId());
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
                        artifactTypeRepository.getWithDVMovies().getId());
        assertThat(hasNumbers.size()).isEqualTo(1);
        assertThat(hasNumbers.get(0).getId()).isNotNull();
        assertThat(hasNumbers.get(0).getTitle()).isEqualTo("Title No Artist");

        assertThat(artifactRepository
                .getArtifactsWithNoMonotonicallyIncreasingTrackNumbers(
                        artifactTypeRepository.getWithDVMusic().getId()).size()).isEqualTo(0);
    }
}
