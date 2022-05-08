package com.romanpulov.odeonwss;

import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryCompositionTests {

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private CompositionRepository compositionRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testInsertGet() {
        //ArtifactType
        ArtifactType artifactType = artifactTypeRepository.findAllById(List.of(100L)).iterator().next();
        Assertions.assertNotNull(artifactType);

        //Artist
        Artist artist = new EntityArtistBuilder().withType("A").withName("Name1").build();
        Artist savedArtist = artistRepository.save(artist);
        Assertions.assertNotNull(savedArtist);
        Assertions.assertNotNull(savedArtist.getId());

        //Artifact
        Artifact artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactType)
                .withArtist(artist)
                .withTitle("Title 1")
                .withYear(1982L)
                .withDuration(73555L)
                .withInsertDate(LocalDate.now().minusDays(1))
                .build();
        Artifact savedArtifact = artifactRepository.save(artifact);
        Assertions.assertNotNull(savedArtifact.getId());
        Assertions.assertEquals(1982L, savedArtifact.getYear());

        //MediaFile
        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withName("Name 1.mp3")
                .withFormat("MP3")
                .withSize(423L)
                .withDuration(6234L)
                .withBitrate(320L)
                .build();
        MediaFile savedMediaFile = mediaFileRepository.save(mediaFile);

        //Composition 1
        Composition wrong_composition = new EntityCompositionBuilder()
                .withTitle("Composition title")
                .build();
        Assertions.assertThrows(Exception.class, () -> compositionRepository.save(wrong_composition));

        //Composition 1
        Composition composition = new EntityCompositionBuilder()
                .withArtifact(savedArtifact)
                .withTitle("Composition title")
                .withDiskNum(1L)
                .withNum(8L)
                .withDuration(123456L)
                .build();
        Composition savedComposition = compositionRepository.save(composition);
        Assertions.assertNotNull(savedComposition.getId());
        Assertions.assertEquals(savedComposition.getArtifact(), savedArtifact);
        Assertions.assertEquals("Composition title", savedComposition.getTitle());
        Assertions.assertEquals(1L, savedComposition.getDiskNum());
        Assertions.assertEquals(8, savedComposition.getNum());
        Assertions.assertEquals(123456L, savedComposition.getDuration());

        //Composition 2
        Composition composition2 = new EntityCompositionBuilder()
                .withArtifact(savedArtifact)
                .withTitle("Composition title 2")
                .withDiskNum(2L)
                .withNum(5L)
                .withDuration(777L)
                .build();
        Composition savedComposition2 = compositionRepository.save(composition2);
        Assertions.assertNotNull(savedComposition2.getId());
        Assertions.assertEquals(savedComposition2.getArtifact(), savedArtifact);
        Assertions.assertEquals("Composition title 2", savedComposition2.getTitle());
        Assertions.assertEquals(2, savedComposition2.getDiskNum());
        Assertions.assertEquals(5, savedComposition2.getNum());
        Assertions.assertEquals(777, savedComposition2.getDuration());
    }

    @Test
    @Order(2)
    void testCascade() {
        Iterable<Composition> compositions = compositionRepository.findAll();
        List<Composition> compositionList = StreamSupport.stream(compositions.spliterator(), false).collect(Collectors.toList());
        Assertions.assertEquals(2, compositionList.size());

        Artist artist = artistRepository.findAll().iterator().next();
        Assertions.assertThrows(Exception.class, () -> artistRepository.delete(artist));

        Artifact artifact = artifactRepository.findAll().iterator().next();
        Assertions.assertNotNull(artifact);

        artifactRepository.delete(artifact);

        Assertions.assertFalse(artifactRepository.findAll().iterator().hasNext());
        // Assertions.assertFalse(compositionRepository.findAll().iterator().hasNext());

    }
}
