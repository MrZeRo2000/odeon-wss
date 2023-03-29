package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.MediaFileEditDTO;
import com.romanpulov.odeonwss.dto.MediaFileTableDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityTrackBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityMediaFileBuilder;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryMediaFileTests {

    private static final Logger log = Logger.getLogger(RepositoryMediaFileTests.class.getSimpleName());

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
    void testCreateGet() {
        Artist artist = new EntityArtistBuilder()
                .withType(ArtistType.ARTIST)
                .withName("Artist 1")
                .build();

        artistRepository.save(artist);

        Artifact artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithMP3())
                .withArtist(artist)
                .withTitle("Title 1")
                .withYear(2022L)
                .withDuration(12346L)
                .build();

        artifactRepository.save(artifact);

        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withName("AAA.mp3")
                .withFormat("MP3")
                .withSize(423L)
                .withDuration(6234L)
                .withBitrate(320L)
                .withMigrationId(230L)
                .build();

        Track track = new EntityTrackBuilder()
                .withArtifact(artifact)
                .withTitle("Track title")
                .withNum(1L)
                .withDiskNum(1L)
                .withDuration(123456L)
                .build();

        trackRepository.save(track);

        MediaFile savedMediaFile = mediaFileRepository.save(mediaFile);
        Assertions.assertNotNull(savedMediaFile.getId());
        Assertions.assertEquals(artifact, mediaFile.getArtifact());
        Assertions.assertEquals(savedMediaFile.getName(), mediaFile.getName());
        Assertions.assertEquals(savedMediaFile.getFormat(), mediaFile.getFormat());
        Assertions.assertEquals(savedMediaFile.getSize(), mediaFile.getSize());
        Assertions.assertEquals(savedMediaFile.getDuration(), mediaFile.getDuration());
        Assertions.assertEquals(savedMediaFile.getBitrate(), mediaFile.getBitrate());
        Assertions.assertEquals(230L, mediaFile.getMigrationId());

        log.info("Saved MediaFile:" + savedMediaFile);

        mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withName("BBB.mp3")
                .withFormat("MP3")
                .withSize(323L)
                .withDuration(7234L)
                .withBitrate(320L)
                .build();
        mediaFileRepository.save(mediaFile);
    }

    @Test
    @Order(2)
    void testCreateMinimal() {
        Artifact artifact = artifactRepository.findById(1L).orElseThrow();

        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withName("CCC.mp3")
                .withFormat("MP3")
                .withSize(0L)
                .build();
        mediaFileRepository.save(mediaFile);
    }

    @Test
    @Order(3)
    void testTrackValidation() {
        List<MediaFileValidationDTO> mediaFileValidation =
                mediaFileRepository.getTrackMediaFileValidationMusic(ArtistType.ARTIST, artifactTypeRepository.getWithMP3());
        Assertions.assertEquals(1, mediaFileValidation.size());
    }

    @Test
    @Order(3)
    void testMediaFileValidation() {
        List<MediaFileValidationDTO> mediaFileValidation =
                mediaFileRepository.getArtifactMediaFileValidationMusic(artifactTypeRepository.getWithMP3());
        Assertions.assertEquals(3, mediaFileValidation.size());
    }

    @Test
    @Order(4)
    void testMediaFileEditDTO() {
        MediaFileEditDTO dto = mediaFileRepository.getMediaFileEditById(1L).orElseThrow();
        Assertions.assertEquals(1L, dto.getId());
        Assertions.assertEquals("AAA.mp3", dto.getName());
    }

    @Test
    @Order(5)
    void testMediaFileTableDTO() {
        List<MediaFileTableDTO> dtoList = mediaFileRepository.getMediaFileTableByArtifact(
                new EntityArtifactBuilder().withId(1L).build()
        );
        Assertions.assertEquals(3, dtoList.size());
        Assertions.assertEquals("AAA.mp3", dtoList.get(0).getName());
        Assertions.assertEquals("BBB.mp3", dtoList.get(1).getName());
        Assertions.assertEquals("CCC.mp3", dtoList.get(2).getName());
    }

    @Test
    @Order(6)
    void testGetIdName() {
        List<IdNameDTO> idNames = mediaFileRepository.findByArtifactOrderByName(
                new EntityArtifactBuilder().withId(1L).build()
        );
        Assertions.assertEquals(3, idNames.size());
        Assertions.assertEquals("AAA.mp3", idNames.get(0).getName());
        Assertions.assertEquals("BBB.mp3", idNames.get(1).getName());
        Assertions.assertEquals("CCC.mp3", idNames.get(2).getName());
    }
}
