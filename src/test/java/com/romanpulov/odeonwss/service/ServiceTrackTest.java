package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.dtobuilder.DVProductDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.MediaFileDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.TrackDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.*;
import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.dto.TrackDTO;
import com.romanpulov.odeonwss.dto.TrackDTOImpl;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import com.romanpulov.odeonwss.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
                        .withName("Origin 1")
                        .build()
        );
        DVProduct product = new EntityDVProductBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMusic())
                .withOrigin(origin)
                .withTitle("Title 1")
                .build();
        dvProductRepository.save(product);

        TrackDTO comp11 = trackService.insert(
            new TrackDTOBuilder()
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
        Assertions.assertEquals(1, comp11.getArtist().getId());
        Assertions.assertEquals(2, comp11.getPerformerArtist().getId());
        Assertions.assertEquals(8, comp11.getDvType().getId());


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
                new TrackDTOBuilder()
                        .withArtifactId(artifact1.getId())
                        .withTitle("Comp 1-2")
                        .withDiskNum(1L)
                        .withNum(5L)
                        .withDuration(12L)
                        .withDvTypeId(7L)
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
    void testGetTableByArtifactTypeId() {
        var table = trackService.getTableByArtifactTypeId(ArtistType.ARTIST, artifactTypeRepository.getWithMP3().getId());
        assertThat(table.size()).isEqualTo(2);
        assertThat(table.get(0).getTitle()).isEqualTo("Comp 1-1");
        assertThat(table.get(0).getDvProduct()).isNull();

        assertThat(table.get(1).getTitle()).isEqualTo("Comp 1-2");
        assertThat(table.get(1).getDvProduct().getId()).isEqualTo(1);
        assertThat(table.get(1).getDvProduct().getTitle()).isEqualTo("Title 1");
    }

    @Test
    @Order(2)
    void testGetTableByProductId() {
        assertThat(trackService.getTableByProductId(2L).size()).isEqualTo(0);

        var table = trackService.getTableByProductId(1L);
        assertThat(table.size()).isEqualTo(1);
        var row = table.getFirst();
        assertThat(row.getArtifact().getId()).isEqualTo(1L);
        assertThat(row.getArtifact().getTitle()).isEqualTo("Title 1");
        assertThat(row.getNum()).isEqualTo(5L);
        assertThat(row.getDuration()).isEqualTo(12L);
        assertThat(row.getTitle()).isEqualTo("Comp 1-2");
        assertThat(row.getDvType().getId()).isEqualTo(7L);
        assertThat(row.getSize()).isEqualTo(776L);
        assertThat(row.getBitRate()).isEqualTo(256L);
        assertThat(row.getMediaFiles().stream().map(MediaFileDTO::getName).collect(Collectors.toList()))
                .isEqualTo(List.of("Comp 1-2.mp3"));
    }

    @Test
    @Order(7)
    @Transactional
    @Rollback(value = false)
    void testRemoveMediaFile() throws Exception {
        Assertions.assertEquals(2, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());

        TrackDTO dto = trackService.getById(1L);
        Assertions.assertEquals(1, trackRepository.findById(1L).orElseThrow().getMediaFiles().size());

        dto.getMediaFiles().clear();
        dto = trackService.update(dto);

        Assertions.assertEquals(0, dto.getMediaFiles().size());
    }


    @Test
    @Order(8)
    @Rollback(value = false)
    void testInsertMediaFile() throws Exception {
        Assertions.assertEquals(0, trackRepository.findByIdWithMediaFiles(1L).orElseThrow().getMediaFiles().size());
        TrackDTOImpl dto = TrackDTOImpl.fromTrackDTO(trackService.getById(1L));

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

        dto.setMediaFiles(List.of(new MediaFileDTOBuilder().withId(mediaFile.getId()).build()));
        TrackDTO updatedDTO = trackService.update(dto);
        Assertions.assertEquals(1, trackRepository.findByIdWithMediaFiles(1L).orElseThrow().getMediaFiles().size());
        assertThat(updatedDTO.getMediaFiles().size()).isEqualTo(1);
    }

    @Test
    @Order(9)
    void testAssignProduct() throws Exception {
        TrackDTOImpl dto = TrackDTOImpl.fromTrackDTO(trackService.getById(2L));
        assertThat(dto.getDvProduct().getId()).isEqualTo(1L);

        dto.setDvProduct(null);
        TrackDTO updatedDTO = trackService.update(dto);

        assertThat(updatedDTO.getDvProduct()).isNull();

        dto.setDvProduct(new DVProductDTOBuilder().withId(1L).build());
        TrackDTO updateDTOProduct = trackService.update(dto);

        assertThat(updateDTOProduct.getDvProduct().getId()).isEqualTo(1L);
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

    @Test
    @Order(11)
    void testResetTrackNumbers() throws Exception {
        var artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
                .withTitle("Movies Title")
                .withDuration(54321L)
                .build();
        artifactRepository.save(artifact);

        var trackNum = new AtomicLong(3L);
        var newTracks = Stream.of("Track 01", "Track 02", "Track 03")
                .map(s ->  new TrackDTOBuilder()
                        .withArtifactId(artifact.getId())
                        .withTitle(s)
                        .withNum(trackNum.getAndIncrement())
                        .withDuration(12L + Math.round(Math.random() * 100))
                        .withDvTypeId(2L)
                        .build()
                )
                .toList();
        for (var track: newTracks) {
            trackService.insert(track);
        }

        var tracksBefore = trackRepository.findAllByArtifact(artifact)
                .stream()
                .sorted(Comparator.comparingLong(t -> t.getNum() == null ? 0 : t.getNum()))
                .toList();
        assertThat(tracksBefore.get(0).getNum()).isEqualTo(3L);
        assertThat(tracksBefore.get(1).getNum()).isEqualTo(4L);
        assertThat(tracksBefore.get(2).getNum()).isEqualTo(5L);

        var rowsAffected = trackService.resetTrackNumbers(artifact.getId());
        assertThat(rowsAffected.getRowsAffected()).isEqualTo(3L);

        var tracksAfter = trackRepository.findAllByArtifact(artifact)
                .stream()
                .sorted(Comparator.comparingLong(t -> t.getNum() == null ? 0 : t.getNum()))
                .toList();
        assertThat(tracksAfter.get(0).getNum()).isEqualTo(1L);
        assertThat(tracksAfter.get(1).getNum()).isEqualTo(2L);
        assertThat(tracksAfter.get(2).getNum()).isEqualTo(3L);
    }

    @Test
    @Order(12)
    void testUpdateDurationsFromMediaFile() throws Exception {
        var artifact = artifactRepository.getAllByArtifactType(artifactTypeRepository.getWithDVMovies()).getFirst();

        var mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withName("Movie")
                .withFormat("MKV")
                .withDuration(400L)
                .withSize(12344L)
                .build();
        mediaFileRepository.save(mediaFile);

        var tracksBefore = trackRepository.findAllByArtifact(artifact);
        assertThat(tracksBefore.size()).isEqualTo(3);
        tracksBefore.get(0).setDuration(0L);
        tracksBefore.get(1).setDuration(null);
        tracksBefore.get(2).setDuration(null);
        tracksBefore.forEach(t -> t.setMediaFiles(Set.of(mediaFile)));
        trackRepository.saveAll(tracksBefore);

        // action
        assertThatThrownBy(
                () -> trackService.updateDurationsFromMediaFile(artifact.getId(), mediaFile.getId(), List.of(61L, -1L, 30L)))
                .isInstanceOf(WrongParameterValueException.class)
                .hasMessageContaining("Invalid chapters found");

        assertThatThrownBy(
                () -> trackService.updateDurationsFromMediaFile(artifact.getId(), mediaFile.getId(), List.of(61L, 788L, 30L)))
                .isInstanceOf(WrongParameterValueException.class)
                .hasMessageContaining("Chapters duration exceeds media file duration");

        assertThatThrownBy(
                () -> trackService.updateDurationsFromMediaFile(artifact.getId(), mediaFile.getId(), List.of(61L)))
                .isInstanceOf(WrongParameterValueException.class)
                .hasMessageContaining("does not correspond to number of tracks");

        assertThatThrownBy(
                () -> trackService.updateDurationsFromMediaFile(artifact.getId(), mediaFile.getId(), List.of(61L, 12L, 4L)))
                .isInstanceOf(WrongParameterValueException.class)
                .hasMessageContaining("does not correspond to number of tracks");

        assertThat(trackService.updateDurationsFromMediaFile(artifact.getId(), mediaFile.getId(), List.of(61L, 12L)).getRowsAffected()).isEqualTo(3L);

        var tracksAfter = trackRepository.findAllByArtifact(artifact);
        assertThat(tracksAfter.get(0).getDuration()).isEqualTo(61L);
        assertThat(tracksAfter.get(1).getDuration()).isEqualTo(12L);
        assertThat(tracksAfter.get(2).getDuration()).isEqualTo(400L - 61 - 12);
    }

    @Test
    @Order(13)
    void testUpdateVideoTypes() throws Exception {
        var artifact = artifactRepository.getAllByArtifactType(artifactTypeRepository.getWithDVMovies()).getFirst();

        var tracksBefore = trackRepository.findAllByArtifact(artifact);
        assertThat(tracksBefore.size()).isEqualTo(3);
        for (Track track: tracksBefore) {
            assertThat(Optional.ofNullable(track.getDvType()).orElseThrow().getId()).isEqualTo(2L);
        }

        trackService.updateVideoTypes(artifact.getId(), 8L);

        var tracksAfter = trackRepository.findAllByArtifact(artifact);
        assertThat(tracksAfter.size()).isEqualTo(3);
        for (Track track: tracksAfter) {
            assertThat(Optional.ofNullable(track.getDvType()).orElseThrow().getId()).isEqualTo(8L);
        }
    }

    @Test
    @Order(14)
    void testUpdateSelectedVideoTypes() throws Exception {
        var artifact = artifactRepository.getAllByArtifactType(artifactTypeRepository.getWithDVMovies()).getFirst();

        var tracksBefore = trackRepository.findAllByArtifact(artifact)
                .stream()
                .sorted(Comparator.comparingLong(t -> t.getId() == null ? 0 : t.getId()))
                .toList();
        assertThat(tracksBefore.size()).isEqualTo(3);
        for (Track track: tracksBefore) {
            assertThat(Optional.ofNullable(track.getDvType()).orElseThrow().getId()).isEqualTo(8L);
        }

        trackService.updateSelectedVideoTypes(
                artifact.getId(),
                List.of(tracksBefore.get(0).getId(), tracksBefore.get(2).getId()),
                7L);

        var tracksAfter = trackRepository.findAllByArtifact(artifact)
                .stream()
                .sorted(Comparator.comparingLong(t -> t.getId() == null ? 0 : t.getId()))
                .toList();
        assertThat(tracksAfter.size()).isEqualTo(3);

        assertThat(Optional.ofNullable(tracksAfter.get(0).getDvType()).orElseThrow().getId()).isEqualTo(7L);
        assertThat(Optional.ofNullable(tracksAfter.get(1).getDvType()).orElseThrow().getId()).isEqualTo(8L);
        assertThat(Optional.ofNullable(tracksAfter.get(2).getDvType()).orElseThrow().getId()).isEqualTo(7L);
    }

    @Test
    @Order(15)
    void testTrackWithTags() throws Exception {
        var artifact = artifactRepository.getAllByArtifactType(artifactTypeRepository.getWithDVMovies()).getFirst();
        var dto = trackService.getTable(artifact.getId()).getFirst();
        assertThat(dto.getTags().isEmpty()).isTrue();

        var trackTags = new TrackDTOBuilder()
                .withId(dto.getId())
                .withTags(List.of("small", "medium", "large"))
                .build();
        trackService.updateTags(trackTags);

        var dto1 = trackService.getTable(artifact.getId()).getFirst();
        assertThat(dto1.getTags().size()).isEqualTo(3);
        assertThat(dto1.getTags().get(0)).isEqualTo("large");
        assertThat(dto1.getTags().get(1)).isEqualTo("medium");
        assertThat(dto1.getTags().get(2)).isEqualTo("small");


        var trackTags2 = new TrackDTOBuilder()
                .withId(dto.getId())
                .withTags(List.of("ugly"))
                .build();
        trackService.updateTags(trackTags2);

        var dto2 = trackService.getTable(artifact.getId()).getFirst();
        assertThat(dto2.getTags().size()).isEqualTo(1);
        assertThat(dto2.getTags().getFirst()).isEqualTo("ugly");

        var trackTags3 = new TrackDTOBuilder()
                .withId(dto.getId())
                .build();
        trackService.updateTags(trackTags3);

        var dto3 = trackService.getTable(artifact.getId()).getFirst();
        assertThat(dto3.getTags().size()).isEqualTo(0);
    }

    @Test
    @Order(16)
    void testUpdateSelectedTags() throws Exception {
        var artifact = artifactRepository.getAllByArtifactType(artifactTypeRepository.getWithDVMovies()).getFirst();
        var tracksBefore = trackService.getTable(artifact.getId());
        assertThat(tracksBefore.size()).isEqualTo(3);

        var trackIds1 = List.of(tracksBefore.get(0).getId(), tracksBefore.get(2).getId());
        var trackIds2 = List.of(tracksBefore.get(1).getId());

        assertThat(trackService.updateSelectedTags(
                artifact.getId(),
                trackIds1,
                List.of("Milk", "Sugar")).getRowsAffected()).isEqualTo(2);
        assertThat(trackService.updateSelectedTags(
                artifact.getId(),
                trackIds2,
                List.of("Cream")).getRowsAffected()).isEqualTo(1);

        var tracksAfter = trackService.getTable(artifact.getId());
        assertThat(tracksAfter.get(0).getTags()).isEqualTo(List.of("Milk", "Sugar"));
        assertThat(tracksAfter.get(1).getTags()).isEqualTo(List.of("Cream"));
        assertThat(tracksAfter.get(2).getTags()).isEqualTo(List.of("Milk", "Sugar"));

        assertThat(trackService.updateSelectedTags(
                artifact.getId(),
                trackIds1,
                List.of()).getRowsAffected()).isEqualTo(2);

        var tracksAfter2 = trackService.getTable(artifact.getId());
        assertThat(tracksAfter2.get(0).getTags()).isEqualTo(List.of());
        assertThat(tracksAfter2.get(1).getTags()).isEqualTo(List.of("Cream"));
        assertThat(tracksAfter2.get(2).getTags()).isEqualTo(List.of());
    }

    @Test
    @Order(17)
    void testGetTableByOptional() {
        var noArgs = trackService.getTableByOptional(null, null);
        assertThat(noArgs.size()).isEqualTo(4);
        assertThat(noArgs.getFirst().getArtifactType().getId()).isEqualTo(artifactTypeRepository.getWithMP3().getId());
        assertThat(noArgs.getFirst().getArtifactType().getName()).isEqualTo(artifactTypeRepository.getWithMP3().getName());
        assertThat(noArgs.getFirst().getArtifact().getTitle()).isEqualTo("Title 1");
        assertThat(noArgs.getFirst().getTitle()).isEqualTo("Comp 1-2");

        var byArtist = trackService.getTableByOptional(null, List.of(1L));
        assertThat(byArtist.size()).isEqualTo(1);

        var byArtifactTypeMP3 = trackService.getTableByOptional(List.of(artifactTypeRepository.getWithMP3().getId()), null);
        assertThat(byArtifactTypeMP3.size()).isEqualTo(1);
        assertThat(byArtifactTypeMP3.getFirst().getTitle()).isEqualTo("Comp 1-2");

        var byArtifactTypeMovies = trackService.getTableByOptional(List.of(artifactTypeRepository.getWithDVMovies().getId()), null);
        assertThat(byArtifactTypeMovies.size()).isEqualTo(3);
        assertThat(byArtifactTypeMovies.get(1).getTitle()).isEqualTo("Track 02");

        var byArtifactTypeMP3Movies = trackService.getTableByOptional(
                List.of(artifactTypeRepository.getWithMP3().getId(), artifactTypeRepository.getWithDVMovies().getId()),
                null);
        assertThat(byArtifactTypeMP3Movies.size()).isEqualTo(4);
    }
}
