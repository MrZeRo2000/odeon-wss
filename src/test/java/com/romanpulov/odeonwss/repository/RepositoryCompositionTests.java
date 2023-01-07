package com.romanpulov.odeonwss.repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.romanpulov.odeonwss.dto.CompositionTableDTO;
import com.romanpulov.odeonwss.dto.CompositionValidationDTO;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityCompositionBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityMediaFileBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
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
        Artist artist = new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name1").build();
        Artist savedArtist = artistRepository.save(artist);
        Assertions.assertNotNull(savedArtist);
        Assertions.assertNotNull(savedArtist.getId());

        //Performer Artist
        Artist performerArtist = new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("PerformerName1").build();
        artistRepository.save(performerArtist);
        Assertions.assertNotNull(performerArtist.getId());

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

        //Artifact 2
        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtifactType(artifactType)
                        .withArtist(artist)
                        .withTitle("Title 2")
                        .withYear(1983L)
                        .withDuration(73556L)
                        .withInsertDate(LocalDate.now().minusDays(5))
                        .build()
        );

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
                .withArtist(artist)
                .withPerformerArtist(performerArtist)
                .withTitle("Composition title")
                .withDiskNum(1L)
                .withNum(8L)
                .withDuration(123456L)
                .withMigrationId(4321L)
                .build();
        Composition savedComposition = compositionRepository.save(composition);
        Assertions.assertNotNull(savedComposition.getId());
        Assertions.assertEquals(savedComposition.getArtifact(), savedArtifact);
        assert savedComposition.getArtist() != null;
        assert savedComposition.getPerformerArtist() != null;
        Assertions.assertEquals("Name1", savedComposition.getArtist().getName());
        Assertions.assertEquals("PerformerName1", savedComposition.getPerformerArtist().getName());
        Assertions.assertEquals("Composition title", savedComposition.getTitle());
        Assertions.assertEquals(1L, savedComposition.getDiskNum());
        Assertions.assertEquals(8, savedComposition.getNum());
        Assertions.assertEquals(123456L, savedComposition.getDuration());
        Assertions.assertEquals(4321L, savedComposition.getMigrationId());

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
        Assertions.assertEquals(3, compositionValidationList.size());
        Assertions.assertEquals(1982, compositionValidationList.get(0).getArtifactYear());
    }

    @Test
    @Order(3)
    void testCompositionTableDTO() {
        List<CompositionTableDTO> compositions = compositionRepository.getCompositionTableByArtifactId(1L);
        Assertions.assertEquals(2, compositions.size());
        Assertions.assertEquals(0, compositionRepository.getCompositionTableByArtifactId(2L).size());
        Assertions.assertEquals(0, compositionRepository.getCompositionTableByArtifactId(3L).size());
    }

    @Test
    @Order(4)
    void testInsertWithSameDiskAndNumShouldFail() {
        Artifact artifact = artifactRepository.findById(1L).orElseThrow();

        Assertions.assertThrows(JpaSystemException.class, () -> {
            compositionRepository.save(
                    new EntityCompositionBuilder()
                            .withArtifact(artifact)
                            .withTitle("Composition title 22")
                            .withDiskNum(2L)
                            .withNum(5L)
                            .withDuration(757L)
                            .build()
            );
        });
    }

    @Test
    @Order(5)
    @Transactional
    @Rollback(false)
    void testUpdateComposition() {
        Composition composition = compositionRepository.findById(1L).orElseThrow();
        composition.setTitle("Composition updated title");
        compositionRepository.save(composition);
    }

    @Test
    @Order(6)
    @Transactional
    @Rollback(false)
    void testInsertWithMediaFileShouldBeOk() throws Exception {
        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifactRepository.findById(1L).orElseThrow())
                .withFormat("mp3")
                .withName("Composition.mp3")
                .withDuration(75L)
                .withSize(5234L)
                .withBitrate(128L)
                .build();

        mediaFileRepository.save(mediaFile);

        Composition composition = compositionRepository.findById(1L).orElseThrow();
        Assertions.assertEquals(0, composition.getMediaFiles().size());

        Set<MediaFile> mediaFiles = composition.getMediaFiles();
        mediaFiles.add(mediaFile);
        composition.setMediaFiles(mediaFiles);
        composition.setTitle("Composition title updated");

        compositionRepository.save(composition);

        composition = compositionRepository.findById(composition.getId()).orElseThrow();
        Assertions.assertEquals(1, composition.getMediaFiles().size());
    }

    @Test
    @Order(7)
    @Transactional
    @Rollback(false)
    void testDeleteMediaFileShouldBeOk() throws Exception {
        Composition composition = compositionRepository.findById(1L).orElseThrow();
        Assertions.assertEquals(1, composition.getMediaFiles().size());

        composition.getMediaFiles().clear();
        compositionRepository.save(composition);

        Assertions.assertEquals(0, composition.getMediaFiles().size());
    }

    @Test
    @Order(100)
    @Disabled("Not actual after DB structure change, to delete")
    void testCascade() {
        Iterable<Composition> compositions = compositionRepository.findAll();
        List<Composition> compositionList = StreamSupport.stream(compositions.spliterator(), false).collect(Collectors.toList());
        Assertions.assertEquals(2, compositionList.size());

        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifactRepository.findById(1L).orElseThrow())
                .withFormat("mp3")
                .withName("File Name")
                .withDuration(123456L)
                .withBitrate(123L)
                .withSize(77777L)
                .build();

        //insert media file
        mediaFileRepository.save(mediaFile);
        Assertions.assertEquals(1, mediaFileRepository.findAllByArtifact(artifactRepository.findById(1L).orElseThrow()).size());
        Assertions.assertEquals(1, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());

        //delete composition
        compositionRepository.delete(compositionList.get(0));
        compositionList = StreamSupport.stream(compositionRepository.findAll().spliterator(), false).collect(Collectors.toList());
        Assertions.assertEquals(1, compositionList.size());

        //media file deleted
        Assertions.assertEquals(0, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());
    }
}
