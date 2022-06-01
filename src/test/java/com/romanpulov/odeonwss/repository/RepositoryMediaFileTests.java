package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entitybuilder.EntityCompositionBuilder;
import com.romanpulov.odeonwss.entitybuilder.EntityMediaFileBuilder;
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

        Composition composition = new EntityCompositionBuilder()
                .withArtifact(artifact)
                .withTitle("Composition title")
                .withNum(1L)
                .withDiskNum(1L)
                .withDuration(123456L)
                .build();

        compositionRepository.save(composition);

        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withComposition(composition)
                .withName("AAA.mp3")
                .withFormat("MP3")
                .withSize(423L)
                .withDuration(6234L)
                .withBitrate(320L)
                .build();

        MediaFile savedMediaFile = mediaFileRepository.save(mediaFile);
        Assertions.assertNotNull(savedMediaFile.getId());
        Assertions.assertEquals(composition, mediaFile.getComposition());
        Assertions.assertEquals(savedMediaFile.getName(), mediaFile.getName());
        Assertions.assertEquals(savedMediaFile.getFormat(), mediaFile.getFormat());
        Assertions.assertEquals(savedMediaFile.getSize(), mediaFile.getSize());
        Assertions.assertEquals(savedMediaFile.getDuration(), mediaFile.getDuration());
        Assertions.assertEquals(savedMediaFile.getBitrate(), mediaFile.getBitrate());

        log.info("Saved MediaFile:" + savedMediaFile);
    }

    @Test
    @Order(2)
    void testDTO() {
        List<MediaFileValidationDTO> mediaFileValidation = mediaFileRepository.getMediaFileValidationMusic(ArtifactType.withMP3());
        Assertions.assertEquals(1, mediaFileValidation.size());
    }
}
