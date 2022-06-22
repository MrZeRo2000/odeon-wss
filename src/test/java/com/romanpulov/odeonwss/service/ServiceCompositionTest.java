package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.dtobuilder.CompositionEditDTOBuilder;
import com.romanpulov.odeonwss.dto.CompositionEditDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
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
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Collectors;
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
    void testInsertShouldBeOk() throws Exception {
        Artist artist = artistRepository.save(new EntityArtistBuilder()
                .withType(ArtistType.ARTIST)
                .withName("Artist 1")
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

        CompositionEditDTO comp11 = compositionService.insert(
            new CompositionEditDTOBuilder()
                    .withArtifact(artifact1)
                    .withTitle("Comp 1-1")
                    .withDiskNum(1L)
                    .withNum(4L)
                    .withDuration(1234L)
                    .withMediaName("Comp 1-1.mp3")
                    .withMediaFormat("mp3")
                    .withMediaBitrate(320L)
                    .withMediaSize(777L)
                    .withMediaDuration(123L)
                    .build()
        );

        Assertions.assertEquals(1L, comp11.getId());
        Assertions.assertEquals("Comp 1-1", comp11.getTitle());
        Assertions.assertEquals(1, comp11.getDiskNum());
        Assertions.assertEquals(4, comp11.getNum());
        Assertions.assertEquals(1234, comp11.getDuration());

        Assertions.assertEquals("Comp 1-1.mp3", comp11.getMediaName());
        Assertions.assertEquals("mp3", comp11.getMediaFormat());
        Assertions.assertEquals(320, comp11.getMediaBitrate());
        Assertions.assertEquals(777, comp11.getMediaSize());
        Assertions.assertEquals(123, comp11.getMediaDuration());

        CompositionEditDTO comp12 = compositionService.insert(
                new CompositionEditDTOBuilder()
                        .withArtifact(artifact1)
                        .withTitle("Comp 1-2")
                        .withDiskNum(1L)
                        .withNum(5L)
                        .withDuration(12L)
                        .withMediaName("Comp 1-2.mp3")
                        .withMediaFormat("mp3")
                        .withMediaBitrate(256L)
                        .withMediaSize(776L)
                        .withMediaDuration(1232L)
                        .build()
        );

        Assertions.assertEquals(2, compositionService.getById(2L).getId());
    }

    @Test
    @Order(2)
    void testRemoveMediaFile() throws Exception {
        Assertions.assertEquals(2, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());

        CompositionEditDTO dto = compositionService.getById(1L);
        dto.setMediaName(null);
        dto = compositionService.update(dto);

        Assertions.assertNull(dto.getMediaName());
        Assertions.assertNull(dto.getMediaFormat());
        Assertions.assertNull(dto.getMediaBitrate());
        Assertions.assertNull(dto.getMediaDuration());
        Assertions.assertNull(dto.getMediaSize());

        Assertions.assertEquals(1, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());
    }

    @Test
    @Order(3)
    void testInsertMediaFile() throws Exception {
        CompositionEditDTO dto = compositionService.getById(1L);
        dto.setMediaName("Comp new.mp3");
        dto.setMediaFormat("mp3");
        dto.setMediaSize(666L);
        dto = compositionService.update(dto);

        Assertions.assertEquals("Comp new.mp3", dto.getMediaName());
        Assertions.assertEquals(2, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());
    }

    @Test
    @Order(4)
    void testDelete() throws Exception {
        Assertions.assertThrows(CommonEntityNotFoundException.class, () -> compositionService.deleteById(5L));

        Assertions.assertEquals(2, compositionRepository.getCompositionsByArtifact(artifactRepository.findById(1L).orElseThrow()).size());
        compositionService.deleteById(1L);
        Assertions.assertEquals(1, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());
        Assertions.assertEquals(1, compositionRepository.getCompositionsByArtifact(artifactRepository.findById(1L).orElseThrow()).size());
    }
}
