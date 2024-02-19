package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.*;
import com.romanpulov.odeonwss.entity.*;
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

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testInsertGet() {
        //ArtifactType
        ArtifactType artifactType = artifactTypeRepository
                .findAllById(List.of(artifactTypeRepository.getWithMP3().getId()))
                .iterator()
                .next();
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
                .withInsertDate(LocalDateTime.now().minusDays(1))
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

        assertThatThrownBy(() -> artifactRepository.findById(3L).orElseThrow()).isInstanceOf(NoSuchElementException.class);
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
}
