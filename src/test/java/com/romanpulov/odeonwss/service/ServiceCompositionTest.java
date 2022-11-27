package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.dtobuilder.CompositionEditDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityMediaFileBuilder;
import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceCompositionTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtifactService artifactService;

    @Autowired
    private CompositionService compositionService;

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Transactional
    @Rollback(value = false)
    void testInsertShouldBeOk() throws Exception {
        Artist artist = artistRepository.save(new EntityArtistBuilder()
                .withType(ArtistType.ARTIST)
                .withName("Artist 1")
                .build()
        );

        Artist performerArtist = artistRepository.save(new EntityArtistBuilder()
                .withType(ArtistType.ARTIST)
                .withName("Performer Artist 1")
                .build()
        );

        Artifact artifact1 = artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtifactType(ArtifactType.withMP3())
                        .withArtist(artist)
                        .withTitle("Title 1")
                        .withYear(2001L)
                        .withDuration(12345L)
                        .build()
        );

        Artifact artifact2 = artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtifactType(ArtifactType.withMP3())
                        .withArtist(artist)
                        .withTitle("Title 2")
                        .withYear(2002L)
                        .withDuration(54321L)
                        .build()
        );

        MediaFile mediaFile11 = mediaFileRepository.save(
                new EntityMediaFileBuilder()
                        .withArtifact(artifact1)
                        .withName("Comp 1-1.mp3")
                        .withFormat("mp3")
                        .withBitrate(320L)
                        .withSize(777L)
                        .withDuration(123L)
                        .build()
        );

        CompositionEditDTO comp11 = compositionService.insert(
            new CompositionEditDTOBuilder()
                    .withArtifactId(artifact1.getId())
                    .withArtistId(artist.getId())
                    .withPerformerArtistId(performerArtist.getId())
                    .withDvTypeId(8L)
                    .withTitle("Comp 1-1")
                    .withDiskNum(1L)
                    .withNum(4L)
                    .withDuration(1234L)
                    .withMediaFileIds(Stream.of(mediaFile11).map(MediaFile::getId).collect(Collectors.toSet()))
                    .build()
        );

        Assertions.assertEquals(1L, comp11.getId());
        Assertions.assertEquals("Comp 1-1", comp11.getTitle());
        Assertions.assertEquals(1, comp11.getDiskNum());
        Assertions.assertEquals(4, comp11.getNum());
        Assertions.assertEquals(1234, comp11.getDuration());
        Assertions.assertEquals(1, comp11.getArtistId());
        Assertions.assertEquals(2, comp11.getPerformerArtistId());
        Assertions.assertEquals(8, comp11.getDvTypeId());


        MediaFile mediaFile12 = mediaFileRepository.save(
                new EntityMediaFileBuilder()
                        .withArtifact(artifact1)
                        .withName("Comp 1-2.mp3")
                        .withFormat("mp3")
                        .withBitrate(256L)
                        .withSize(776L)
                        .withDuration(1232L)
                        .build()
        );

        CompositionEditDTO comp12 = compositionService.insert(
                new CompositionEditDTOBuilder()
                        .withArtifactId(artifact1.getId())
                        .withTitle("Comp 1-2")
                        .withDiskNum(1L)
                        .withNum(5L)
                        .withDuration(12L)
                        .withMediaFileIds(Stream.of(mediaFile12).map(MediaFile::getId).collect(Collectors.toSet()))
                        .build()
        );

        Assertions.assertEquals(2, compositionService.getById(2L).getId());
    }

    @Test
    @Order(2)
    @Transactional
    @Rollback(value = false)
    void testRemoveMediaFile() throws Exception {
        Assertions.assertEquals(2, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());

        CompositionEditDTO dto = compositionService.getById(1L);
        Assertions.assertEquals(1, compositionRepository.findById(1L).orElseThrow().getMediaFiles().size());

        dto.getMediaFileIds().clear();
        dto = compositionService.update(dto);

        Assertions.assertEquals(0, dto.getMediaFileIds().size());
    }


    @Test
    @Order(3)
    @Transactional
    @Rollback(value = false)
    void testInsertMediaFile() throws Exception {
        Assertions.assertEquals(0, compositionRepository.findById(1L).orElseThrow().getMediaFiles().size());
        CompositionEditDTO dto = compositionService.getById(1L);

        MediaFile mediaFile = mediaFileRepository.save(
                new EntityMediaFileBuilder()
                        .withArtifact(artifactRepository.findById(1L).orElseThrow())
                        .withName("Comp new.mp3")
                        .withFormat("mp3")
                        .withBitrate(320L)
                        .withDuration(12355L)
                        .withSize(44433L)
                        .build()
        );

        dto.setMediaFiles(Set.of(mediaFile.getId()));
        dto = compositionService.update(dto);
        Assertions.assertEquals(1, compositionRepository.findById(1L).orElseThrow().getMediaFiles().size());
    }
    @Test
    @Order(4)
    void testDelete() throws Exception {
        Assertions.assertThrows(CommonEntityNotFoundException.class, () -> compositionService.deleteById(5L));

        Assertions.assertEquals(2, compositionRepository.findAllByArtifact(artifactRepository.findById(1L).orElseThrow()).size());
        compositionService.deleteById(1L);
        //TODO orphan deletion procedure
        //Assertions.assertEquals(1, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());
        Assertions.assertEquals(1, compositionRepository.findAllByArtifact(artifactRepository.findById(1L).orElseThrow()).size());
    }
}
