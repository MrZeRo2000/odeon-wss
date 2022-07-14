package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.MediaFileEditDTO;
import com.romanpulov.odeonwss.dto.MediaFileTableDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityCompositionBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityMediaFileBuilder;
import com.romanpulov.odeonwss.view.IdNameView;
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
    ArtistRepository artistRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    CompositionRepository compositionRepository;

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
                .withArtifactType(ArtifactType.withMP3())
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
                .build();

        Composition composition = new EntityCompositionBuilder()
                .withArtifact(artifact)
                .withTitle("Composition title")
                .withNum(1L)
                .withDiskNum(1L)
                .withDuration(123456L)
                .build();

        compositionRepository.save(composition);

        MediaFile savedMediaFile = mediaFileRepository.save(mediaFile);
        Assertions.assertNotNull(savedMediaFile.getId());
        Assertions.assertEquals(artifact, mediaFile.getArtifact());
        Assertions.assertEquals(savedMediaFile.getName(), mediaFile.getName());
        Assertions.assertEquals(savedMediaFile.getFormat(), mediaFile.getFormat());
        Assertions.assertEquals(savedMediaFile.getSize(), mediaFile.getSize());
        Assertions.assertEquals(savedMediaFile.getDuration(), mediaFile.getDuration());
        Assertions.assertEquals(savedMediaFile.getBitrate(), mediaFile.getBitrate());

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
    void testCompositionValidation() {
        List<MediaFileValidationDTO> mediaFileValidation = mediaFileRepository.getCompositionMediaFileValidationMusic(ArtifactType.withMP3());
        Assertions.assertEquals(1, mediaFileValidation.size());
    }

    @Test
    @Order(2)
    void testMediaFileValidation() {
        List<MediaFileValidationDTO> mediaFileValidation = mediaFileRepository.getMediaFileValidationMusic(ArtifactType.withMP3());
        Assertions.assertEquals(2, mediaFileValidation.size());
    }

    @Test
    @Order(3)
    void testMediaFileEditDTO() {
        MediaFileEditDTO dto = mediaFileRepository.getMediaFileEditById(1L).orElseThrow();
        Assertions.assertEquals(1L, dto.getId());
        Assertions.assertEquals("AAA.mp3", dto.getName());
    }

    @Test
    @Order(4)
    void testMediaFileTableDTO() {
        List<MediaFileTableDTO> dtoList = mediaFileRepository.getMediaFileTableByArtifact(
                new EntityArtifactBuilder().withId(1L).build()
        );
        Assertions.assertEquals(2, dtoList.size());
        Assertions.assertEquals("AAA.mp3", dtoList.get(0).getName());
        Assertions.assertEquals("BBB.mp3", dtoList.get(1).getName());
    }

    @Test
    @Order(5)
    void testGetIdName() {
        List<IdNameView> idNames = mediaFileRepository.findByArtifactOrderByName(
                new EntityArtifactBuilder().withId(1L).build()
        );
        Assertions.assertEquals(2, idNames.size());
        Assertions.assertEquals("AAA.mp3", idNames.get(0).getName());
        Assertions.assertEquals("BBB.mp3", idNames.get(1).getName());
    }
}
