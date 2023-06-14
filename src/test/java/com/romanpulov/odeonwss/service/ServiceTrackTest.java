package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.dtobuilder.TrackEditDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.*;
import com.romanpulov.odeonwss.dto.TrackEditDTO;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTrackTest {

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private DVOriginRepository dvOriginRepository;

    @Autowired
    private DVProductRepository dvProductRepository;

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
                        .withArtifactType(artifactTypeRepository.getWithMP3())
                        .withArtist(artist)
                        .withTitle("Title 1")
                        .withYear(2001L)
                        .withDuration(12345L)
                        .build()
        );

        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtifactType(artifactTypeRepository.getWithMP3())
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

        // create product
        DVOrigin origin = dvOriginRepository.save(
                new EntityDVOriginBuilder()
                        .withId(1)
                        .withName("Origin 1")
                        .build()
        );
        DVProduct product = new EntityDVProductBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMusic())
                .withOrigin(origin)
                .withTitle("Title 1")
                .build();
        dvProductRepository.save(product);

        TrackEditDTO comp11 = trackService.insert(
            new TrackEditDTOBuilder()
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

        trackService.insert(
                new TrackEditDTOBuilder()
                        .withArtifactId(artifact1.getId())
                        .withTitle("Comp 1-2")
                        .withDiskNum(1L)
                        .withNum(5L)
                        .withDuration(12L)
                        .withMediaFileIds(Stream.of(mediaFile12).map(MediaFile::getId).collect(Collectors.toSet()))
                        .withDvProductId(product.getId())
                        .build()
        );

        Assertions.assertEquals(2, trackService.getById(2L).getId());
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void testGetTable() throws Exception {
        var table = trackService.getTable(1L);
        assertThat(table.size()).isEqualTo(2);
        assertThat(table.get(0).getTitle()).isEqualTo("Comp 1-1");
        assertThat(table.get(1).getTitle()).isEqualTo("Comp 1-2");
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void testGetTableByArtifactTypeId() {
        var table = trackService.getTableByArtifactTypeId(artifactTypeRepository.getWithMP3().getId());
        assertThat(table.size()).isEqualTo(2);
        assertThat(table.get(0).getTitle()).isEqualTo("Comp 1-1");
        assertThat(table.get(0).getDvProduct()).isNull();

        assertThat(table.get(1).getTitle()).isEqualTo("Comp 1-2");
        assertThat(table.get(1).getDvProduct().getId()).isEqualTo(1);
        assertThat(table.get(1).getDvProduct().getTitle()).isEqualTo("Title 1");
    }

    @Test
    @Order(7)
    @Transactional
    @Rollback(value = false)
    void testRemoveMediaFile() throws Exception {
        Assertions.assertEquals(2, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());

        TrackEditDTO dto = trackService.getById(1L);
        Assertions.assertEquals(1, trackRepository.findById(1L).orElseThrow().getMediaFiles().size());

        dto.getMediaFileIds().clear();
        dto = trackService.update(dto);

        Assertions.assertEquals(0, dto.getMediaFileIds().size());
    }


    @Test
    @Order(8)
    @Transactional
    @Rollback(value = false)
    void testInsertMediaFile() throws Exception {
        Assertions.assertEquals(0, trackRepository.findById(1L).orElseThrow().getMediaFiles().size());
        TrackEditDTO dto = trackService.getById(1L);

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
        dto = trackService.update(dto);
        Assertions.assertEquals(1, trackRepository.findById(1L).orElseThrow().getMediaFiles().size());
        assertThat(dto.getMediaFileIds().size()).isEqualTo(1);
    }

    @Test
    @Order(9)
    void testAssignProduct() throws Exception {
        TrackEditDTO dto = trackService.getById(2L);
        assertThat(dto.getDvProductId()).isEqualTo(1L);

        dto.setDvProductId(null);
        dto = trackService.update(dto);

        assertThat(dto.getDvProductId()).isNull();

        dto.setDvProductId(1L);
        dto = trackService.update(dto);

        assertThat(dto.getDvProductId()).isEqualTo(1L);
    }


    @Test
    @Order(10)
    void testDelete() throws Exception {
        Assertions.assertThrows(CommonEntityNotFoundException.class, () -> trackService.deleteById(5L));

        Assertions.assertEquals(2, trackRepository.findAllByArtifact(artifactRepository.findById(1L).orElseThrow()).size());
        trackService.deleteById(1L);
        //TODO orphan deletion procedure
        //Assertions.assertEquals(1, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());
        Assertions.assertEquals(1, trackRepository.findAllByArtifact(artifactRepository.findById(1L).orElseThrow()).size());
    }
}
