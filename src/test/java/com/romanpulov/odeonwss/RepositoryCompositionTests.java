package com.romanpulov.odeonwss;

import com.romanpulov.odeonwss.dto.CompositionValidationDTO;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
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
        ArtifactType artifactType = artifactTypeRepository.findAllById(List.of(ArtifactType.withMP3().getId())).iterator().next();
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

        //validation DTO without composition
        List<CompositionValidationDTO> compositionValidationList = compositionRepository.getCompositionValidationMusic(ArtifactType.withMP3());
        Assertions.assertNotNull(compositionValidationList.get(0).getArtistName());
        Assertions.assertNotNull(compositionValidationList.get(0).getArtifactTitle());
        Assertions.assertNull(compositionValidationList.get(0).getCompositionNum());
        Assertions.assertNull(compositionValidationList.get(0).getCompositionTitle());

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
    void testValidationDTO() {
        List<CompositionValidationDTO> compositionValidationList = compositionRepository.getCompositionValidationMusic(ArtifactType.withMP3());
        Assertions.assertEquals(2, compositionValidationList.size());
        Assertions.assertEquals(1982, compositionValidationList.get(0).getArtifactYear());
    }

    @Test
    @Order(3)
    void testCascade() {
        Iterable<Composition> compositions = compositionRepository.findAll();
        List<Composition> compositionList = StreamSupport.stream(compositions.spliterator(), false).collect(Collectors.toList());
        Assertions.assertEquals(2, compositionList.size());

        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withComposition(compositionList.get(0))
                .withFormat("mp3")
                .withName("File Name")
                .withDuration(123456L)
                .withBitrate(123L)
                .withSize(77777L)
                .build();

        //insert media file
        mediaFileRepository.save(mediaFile);
        Assertions.assertEquals(1, mediaFileRepository.findAllByComposition(compositionList.get(0)).size());
        Assertions.assertEquals(1, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());

        //delete composition
        compositionRepository.delete(compositionList.get(0));
        compositionList = StreamSupport.stream(compositionRepository.findAll().spliterator(), false).collect(Collectors.toList());
        Assertions.assertEquals(1, compositionList.size());

        //media file deleted
        Assertions.assertEquals(0, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());
    }
}
