package com.romanpulov.odeonwss.repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.romanpulov.odeonwss.dto.TrackTableDTO;
import com.romanpulov.odeonwss.dto.TrackValidationDTO;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityTrackBuilder;
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

        //validation DTO without track
        List<TrackValidationDTO> trackValidationList = trackRepository.getTrackValidationMusic(ArtifactType.withMP3());
        Assertions.assertNotNull(trackValidationList.get(0).getArtistName());
        Assertions.assertNotNull(trackValidationList.get(0).getArtifactTitle());
        Assertions.assertNull(trackValidationList.get(0).getTrackNum());
        Assertions.assertNull(trackValidationList.get(0).getTrackTitle());

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

        Assertions.assertEquals(2, trackRepository.getTracksByArtifactType(ArtifactType.withMP3()).size());
        Assertions.assertEquals(0, trackRepository.getTracksByArtifactType(ArtifactType.withDVMovies()).size());
    }

    @Test
    @Order(2)
    void testValidationDTO() {
        List<TrackValidationDTO> trackValidationList = trackRepository.getTrackValidationMusic(ArtifactType.withMP3());
        Assertions.assertEquals(3, trackValidationList.size());
        Assertions.assertEquals(1982, trackValidationList.get(0).getArtifactYear());
    }

    @Test
    @Order(3)
    void testTrackTableDTO() {
        List<TrackTableDTO> tracks = trackRepository.getTrackTableByArtifactId(1L);
        Assertions.assertEquals(2, tracks.size());
        Assertions.assertEquals(0, trackRepository.getTrackTableByArtifactId(2L).size());
        Assertions.assertEquals(0, trackRepository.getTrackTableByArtifactId(3L).size());
    }

    @Test
    @Order(4)
    void testInsertWithSameDiskAndNumShouldFail() {
        Artifact artifact = artifactRepository.findById(1L).orElseThrow();

        Assertions.assertThrows(JpaSystemException.class, () -> {
            trackRepository.save(
                    new EntityTrackBuilder()
                            .withArtifact(artifact)
                            .withTitle("Track title 22")
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
    void testUpdateTrack() {
        Track track = trackRepository.findById(1L).orElseThrow();
        track.setTitle("Track updated title");
        trackRepository.save(track);
    }

    @Test
    @Order(6)
    @Transactional
    @Rollback(false)
    void testInsertWithMediaFileShouldBeOk() throws Exception {
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
    }

    @Test
    @Order(7)
    @Transactional
    @Rollback(false)
    void testDeleteMediaFileShouldBeOk() throws Exception {
        Track track = trackRepository.findById(1L).orElseThrow();
        Assertions.assertEquals(1, track.getMediaFiles().size());

        track.getMediaFiles().clear();
        trackRepository.save(track);

        Assertions.assertEquals(0, track.getMediaFiles().size());
    }

    @Test
    @Order(100)
    @Disabled("Not actual after DB structure change, to delete")
    void testCascade() {
        Iterable<Track> tracks = trackRepository.findAll();
        List<Track> trackList = StreamSupport.stream(tracks.spliterator(), false).collect(Collectors.toList());
        Assertions.assertEquals(2, trackList.size());

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

        //delete track
        trackRepository.delete(trackList.get(0));
        trackList = StreamSupport.stream(trackRepository.findAll().spliterator(), false).collect(Collectors.toList());
        Assertions.assertEquals(1, trackList.size());

        //media file deleted
        Assertions.assertEquals(0, StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count());
    }
}