package com.romanpulov.odeonwss;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

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
    CompositionRepository compositionRepository;

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
        ArtifactType artifactType = artifactTypeRepository.findAllById(List.of(100L)).iterator().next();
        Assertions.assertNotNull(artifactType);

        //Artist
        Artist artist = new EntityArtistBuilder().withType("A").withName("Name1").build();
        artistRepository.save(artist);
        Assertions.assertNotNull(artist);
        Assertions.assertNotNull(artist.getId());

        Artifact artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactType)
                .withArtist(artist)
                .withTitle("Title 1")
                .withYear(2000L)
                .withDuration(54334L)
                .withInsertDate(LocalDate.now().minusDays(1))
                .build();

        artifactRepository.save(artifact);

        Assertions.assertNotNull(artifact);
        Assertions.assertNotNull(artifact.getId());
        Assertions.assertEquals(artist, artifact.getArtist());
        Assertions.assertEquals(artifactType, artifact.getArtifactType());
        Assertions.assertEquals("Title 1", artifact.getTitle());
        Assertions.assertEquals(2000L, artifact.getYear());
        Assertions.assertEquals(54334L, artifact.getDuration());

        log.info("Saved artifact summary:" + artifact);

        Assertions.assertEquals(1, artifactRepository.getArtifactsByArtist(artist).size());
    }

    @Test
    @Order(3)
    void testCascade() {
        Artifact artifact = artifactRepository.findAll().iterator().next();
        Assertions.assertNotNull(artifact);

        Composition composition = new EntityCompositionBuilder()
                .withArtifact(artifact)
                .withTitle("Artifact title")
                .withDiskNum(1L)
                .withNum(1L)
                .withDuration(12345L)
                .build();

        compositionRepository.save(composition);

        composition = new EntityCompositionBuilder()
                .withArtifact(artifact)
                .withTitle("Artifact title 2")
                .withDiskNum(1L)
                .withNum(3L)
                .withDuration(123458L)
                .build();

        compositionRepository.save(composition);

        Assertions.assertEquals(2, compositionRepository.getCompositionsByArtifact(artifact).size());

        artifactRepository.delete(artifact);
        Assertions.assertEquals(0, compositionRepository.getCompositionsByArtifact(artifact).size());
    }
}
