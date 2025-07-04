package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.*;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.entity.Tag;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryTrackTests {

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private DVProductRepository dvProductRepository;

    @Autowired
    private DVOriginRepository dvOriginRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testInsertGet() {
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
                .withArtifactType(artifactTypeRepository.getWithMP3())
                .withArtist(artist)
                .withTitle("Title 1")
                .withYear(1982L)
                .withDuration(73555L)
                .withInsertDate(LocalDateTime.now().minusDays(1))
                .build();
        Artifact savedArtifact = artifactRepository.save(artifact);
        Assertions.assertNotNull(savedArtifact.getId());
        Assertions.assertEquals(1982L, savedArtifact.getYear());

        //Artifact 2
        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtifactType(artifactTypeRepository.getWithMP3())
                        .withArtist(artist)
                        .withTitle("Title 2")
                        .withYear(1983L)
                        .withDuration(73556L)
                        .withInsertDate(LocalDateTime.now().minusDays(5))
                        .build()
        );

        //Artifact 3
        Artifact artifactMovie = artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtifactType(artifactTypeRepository.getWithDVMovies())
                        .withTitle("Movie 3")
                        .withYear(1983L)
                        .withDuration(73556L)
                        .withInsertDate(LocalDateTime.now().minusDays(5))
                        .build()
        );

        //Track 1
        Track wrong_track = new EntityTrackBuilder()
                .withTitle("Track title")
                .build();
        Assertions.assertThrows(Exception.class, () -> trackRepository.save(wrong_track));

        //Track 1
        Track track = new EntityTrackBuilder()
                .withArtifact(savedArtifact)
                .withArtist(artist)
                .withPerformerArtist(performerArtist)
                .withTitle("Track title")
                .withDiskNum(1L)
                .withNum(8L)
                .withDuration(123456L)
                .withMigrationId(4321L)
                .build();
        Track savedTrack = trackRepository.save(track);
        Assertions.assertNotNull(savedTrack.getId());
        Assertions.assertEquals(savedTrack.getArtifact(), savedArtifact);
        assert savedTrack.getArtist() != null;
        assert savedTrack.getPerformerArtist() != null;
        Assertions.assertEquals("Name1", savedTrack.getArtist().getName());
        Assertions.assertEquals("PerformerName1", savedTrack.getPerformerArtist().getName());
        Assertions.assertEquals("Track title", savedTrack.getTitle());
        Assertions.assertEquals(1L, savedTrack.getDiskNum());
        Assertions.assertEquals(8, savedTrack.getNum());
        Assertions.assertEquals(123456L, savedTrack.getDuration());
        Assertions.assertEquals(4321L, savedTrack.getMigrationId());

        //Track 2
        Track track2 = new EntityTrackBuilder()
                .withArtifact(savedArtifact)
                .withTitle("Track title 2")
                .withDiskNum(2L)
                .withNum(5L)
                .withDuration(777L)
                .build();
        Track savedTrack2 = trackRepository.save(track2);
        Assertions.assertNotNull(savedTrack2.getId());
        Assertions.assertEquals(savedTrack2.getArtifact(), savedArtifact);
        Assertions.assertEquals("Track title 2", savedTrack2.getTitle());
        Assertions.assertEquals(2, savedTrack2.getDiskNum());
        Assertions.assertEquals(5, savedTrack2.getNum());
        Assertions.assertEquals(777, savedTrack2.getDuration());

        Assertions.assertEquals(2, trackRepository.getTracksByArtifactType(artifactTypeRepository.getWithMP3()).size());
        Assertions.assertEquals(0, trackRepository.getTracksByArtifactType(artifactTypeRepository.getWithDVMovies()).size());

        //Track 3
        Track track3 = new EntityTrackBuilder()
                .withArtifact(artifactMovie)
                .withTitle("Movie 3")
                .withNum(1L)
                .withDuration(77457L)
                .build();
        trackRepository.save(track3);
    }

    @Test
    @Order(3)
    void testTrackTableDTO() {
        Artifact artifact1 = artifactRepository.findById(1L).orElseThrow();
        var flatTracks1 = trackRepository.findAllFlatDTOByArtifactId(artifact1.getId());
        assertThat(flatTracks1.size()).isEqualTo(2);

        Artifact artifact2 = artifactRepository.findById(2L).orElseThrow();
        var flatTracks2 = trackRepository.findAllFlatDTOByArtifactId(artifact2.getId());
        assertThat(flatTracks2.size()).isEqualTo(0);

        assertThatThrownBy(() -> artifactRepository.findById(4L).orElseThrow()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @Order(4)
    void testInsertWithSameDiskAndNumShouldFail() {
        Artifact artifact = artifactRepository.findById(1L).orElseThrow();

        Assertions.assertThrows(JpaSystemException.class, () ->
            trackRepository.save(
                    new EntityTrackBuilder()
                            .withArtifact(artifact)
                            .withTitle("Track title 22")
                            .withDiskNum(2L)
                            .withNum(5L)
                            .withDuration(757L)
                            .build()
            )
        );
    }

    @Test
    @Order(5)
    @Transactional
    @Rollback(false)
    void testUpdateTrack() {
        Track track = trackRepository.findById(1L).orElseThrow();
        track.setTitle("Track updated title");
        trackRepository.save(track);
    }

    @Test
    @Order(6)
    @Transactional
    @Rollback(false)
    void testInsertWithMediaFileShouldBeOk() {
        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifactRepository.findById(1L).orElseThrow())
                .withFormat("mp3")
                .withName("Track.mp3")
                .withDuration(75L)
                .withSize(5234L)
                .withBitrate(128L)
                .build();

        mediaFileRepository.save(mediaFile);

        Track track = trackRepository.findById(1L).orElseThrow();
        Assertions.assertEquals(0, track.getMediaFiles().size());

        Set<MediaFile> mediaFiles = track.getMediaFiles();
        mediaFiles.add(mediaFile);
        track.setMediaFiles(mediaFiles);
        track.setTitle("Track title updated");

        trackRepository.save(track);

        track = trackRepository.findById(track.getId()).orElseThrow();
        Assertions.assertEquals(1, track.getMediaFiles().size());

        var trackDTO = trackRepository.findAllFlatDTOByArtifactId(1L).get(0);
        assertThat(trackDTO.getMediaFileId()).isEqualTo(1L);
        assertThat(trackDTO.getMediaFileName()).isEqualTo("Track.mp3");
    }

    @Test
    @Order(7)
    @Transactional
    @Rollback(false)
    void testDeleteMediaFileShouldBeOk() {
        Track track = trackRepository.findById(1L).orElseThrow();
        Assertions.assertEquals(1, track.getMediaFiles().size());

        track.getMediaFiles().clear();
        trackRepository.save(track);

        Assertions.assertEquals(0, track.getMediaFiles().size());
    }

    @Test
    @Order(8)
    void testCreateWithProduct() {
        assertThat(dvProductRepository.findAll().size()).isEqualTo(0);

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

        assertThat(dvProductRepository.findAll().size()).isEqualTo(1);

        Track track = trackRepository.findByIdWithProducts(1L).orElseThrow();
        assertThat(track.getDvProducts().size()).isEqualTo(0);

        track.getDvProducts().add(product);
        trackRepository.save(track);

        Track savedTrack = trackRepository.findByIdWithProducts(1L).orElseThrow();
        assertThat(savedTrack.getDvProducts().size()).isEqualTo(1);

        var trackDTO1 = trackRepository.findAllFlatDTOByArtifactId(savedTrack.getArtifact().getId())
                .stream()
                .filter(v -> v.getId().equals(1L))
                .findFirst().orElseThrow();
        assertThat(trackDTO1.getDvProductId()).isEqualTo(product.getId());

        var trackDTO2 = trackRepository.findAllFlatDTOByArtifactId(savedTrack.getArtifact().getId())
                .stream()
                .filter(v -> v.getId().equals(2L))
                .findFirst().orElseThrow();
        assertThat(trackDTO2.getDvProductId()).isNull();

        savedTrack.setDvProducts(Set.of());
        trackRepository.save(savedTrack);

        Track deletedTrack = trackRepository.findByIdWithProducts(1L).orElseThrow();
        assertThat(deletedTrack.getDvProducts().size()).isEqualTo(0);
    }

    @Test
    @Order(9)
    void testFindAllByOptional() {
        var noArgs = trackRepository.findAllFlatDTOByOptional(
                0L, null,
                0L, null
        );
        assertThat(noArgs.size()).isEqualTo(3);

        assertThat(noArgs.get(0).getArtifactTypeId()).isEqualTo(artifactTypeRepository.getWithDVMovies().getId());
        assertThat(noArgs.get(0).getArtifactTypeName()).isEqualTo(artifactTypeRepository.getWithDVMovies().getName());
        assertThat(noArgs.get(0).getArtistId()).isNull();
        assertThat(noArgs.get(0).getTitle()).isEqualTo("Movie 3");
        assertThat(noArgs.get(0).getDiskNum()).isNull();
        assertThat(noArgs.get(0).getNum()).isEqualTo(1L);
        assertThat(noArgs.get(0).getDuration()).isEqualTo(77457L);

        assertThat(noArgs.get(1).getArtifactTypeId()).isEqualTo(artifactTypeRepository.getWithMP3().getId());
        assertThat(noArgs.get(1).getArtifactTypeName()).isEqualTo(artifactTypeRepository.getWithMP3().getName());

        assertThat(noArgs.get(2).getArtistId()).isEqualTo(1L);
        assertThat(noArgs.get(2).getArtistName()).isEqualTo("Name1");

        var byArtist = trackRepository.findAllFlatDTOByOptional(
                0L, null,
                1L, List.of(1L));
        assertThat(byArtist.size()).isEqualTo(2);
        assertThat(byArtist.get(0).getTitle()).isEqualTo("Track title 2");
        assertThat(byArtist.get(0).getArtistId()).isEqualTo(1L);
        assertThat(byArtist.get(0).getArtistName()).isEqualTo("Name1");
        assertThat(byArtist.get(1).getTitle()).isEqualTo("Track title updated");
        assertThat(byArtist.get(1).getArtistId()).isEqualTo(1L);
        assertThat(byArtist.get(1).getArtistName()).isEqualTo("Name1");

        var byArtifactTypeMP3 = trackRepository.findAllFlatDTOByOptional(
                1L, List.of(artifactTypeRepository.getWithMP3().getId()),
                0L, null
        );
        assertThat(byArtifactTypeMP3.size()).isEqualTo(2);

        var byArtifactTypeMovies = trackRepository.findAllFlatDTOByOptional(
                1L, List.of(artifactTypeRepository.getWithDVMovies().getId()),
                0L, null);
        assertThat(byArtifactTypeMovies.size()).isEqualTo(1);
        assertThat(byArtifactTypeMovies.get(0).getTitle()).isEqualTo("Movie 3");

        var byArtifactTypeAndArtist = trackRepository.findAllFlatDTOByOptional(
                1L, List.of(artifactTypeRepository.getWithMP3().getId()),
                1L, List.of(1L));
        assertThat(byArtifactTypeAndArtist.size()).isEqualTo(2);
    }

    @Test
    @Order(10)
    void testTrackTags() {
        tagRepository.deleteAll();

        Tag tagRed = new EntityTagBuilder().withName("red").build();
        tagRepository.save(tagRed);

        Tag tagYellow = new EntityTagBuilder().withName("yellow").build();
        tagRepository.save(tagYellow);

        Tag tagGreen = new EntityTagBuilder().withName("green").build();
        tagRepository.save(tagGreen);

        Track track1 = trackRepository.findById(1L).orElseThrow();
        track1.setTags(Set.of(tagRed, tagYellow));
        trackRepository.save(track1);

        Track track2 = trackRepository.findById(2L).orElseThrow();
        track2.setTags(Set.of(tagGreen));
        trackRepository.save(track2);

        var tracks1 = trackRepository.findAllFlatDTOByArtifactId(1L);
        assertThat(tracks1.size()).isEqualTo(3);
        assertThat(tracks1.get(0).getId()).isEqualTo(1L);
        assertThat(tracks1.get(0).getTitle()).isEqualTo("Track title updated");
        assertThat(tracks1.get(0).getTagName()).isEqualTo("red");
        assertThat(tracks1.get(1).getTagName()).isEqualTo("yellow");

        var tracks3 = trackRepository.findAllFlatDTOByArtifactId(3L);
        assertThat(tracks3.size()).isEqualTo(1);
        assertThat(tracks3.get(0).getTagName()).isNull();

        track1.setTags(Set.of(tagGreen));
        trackRepository.save(track1);

        var tracks12 = trackRepository.findAllFlatDTOByArtifactId(1L);
        assertThat(tracks12.size()).isEqualTo(2);
        assertThat(tracks12.get(0).getId()).isEqualTo(1L);
        assertThat(tracks12.get(0).getTitle()).isEqualTo("Track title updated");
        assertThat(tracks12.get(0).getTagName()).isEqualTo("green");

        track1.setTags(Set.of());
        trackRepository.save(track1);

        var tracks13 = trackRepository.findAllFlatDTOByArtifactId(1L);
        assertThat(tracks13.size()).isEqualTo(2);
        assertThat(tracks13.get(0).getId()).isEqualTo(1L);
        assertThat(tracks13.get(0).getTitle()).isEqualTo("Track title updated");
        assertThat(tracks13.get(0).getTagName()).isNull();
    }
}
