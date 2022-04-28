package com.romanpulov.odeonwss;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/schema.sql", "/data.sql"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryArtifactTests {

    private static final Logger log = Logger.getLogger(RepositoryArtifactTests.class.getSimpleName());

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Test
    @Order(1)
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
        artist = artistRepository.save(artist);
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

        Artifact savedArtifact = artifactRepository.save(artifact);

        Assertions.assertNotNull(savedArtifact);
        Assertions.assertNotNull(savedArtifact.getId());
        Assertions.assertEquals(savedArtifact.getArtist(), artist);
        Assertions.assertEquals(savedArtifact.getArtifactType(), artifactType);
        Assertions.assertEquals(savedArtifact.getTitle(), "Title 1");
        Assertions.assertEquals(savedArtifact.getYear(), 2000L);
        Assertions.assertEquals(savedArtifact.getDuration(), 54334L);

        log.info("Saved artifact summary:" + savedArtifact);

        Assertions.assertEquals(1, artifactRepository.getArtifactsByArtist(artist).size());
    }
}
