package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessLoadLATest {

    private static final Logger log = Logger.getLogger(ServiceProcessLoadLATest.class.getSimpleName());
    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.LA_LOADER;
    private static final String[] ARTIST_LIST = {
            "Evanescence",
            "Pink Floyd",
            "Therapy",
            "Tori Amos",
            "Abigail Williams",
            "Agua De Annique",
            "Christina Aguilera",
            "The Sisters Of Mercy"
    };

    @Autowired
    EntityManager em;

    @Autowired
    private ProcessService processService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testNoArtists() {
        List<ProcessDetail> processDetail;

        // warnings - no artists exist
        processService.executeProcessor(PROCESSOR_TYPE, null);
        processDetail = processService.getProcessInfo().getProcessDetails();

        Assertions.assertEquals(11, processDetail.size());
        Assertions.assertEquals(ProcessingStatus.WARNING, processService.getProcessInfo().getProcessingStatus());

        List<ProcessDetail> expectedProcessDetails = Stream.of(ARTIST_LIST)
                .map(v -> new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artist " + v + " not found"),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, v))
                )
                .collect(Collectors.toList());

        // check processing progress
        for (int i = 1; i < 9; i++) {
            ProcessingAction pa = processDetail.get(i).getProcessingAction();
            Assertions.assertNotNull(pa);
            Assertions.assertEquals(ProcessingActionType.ADD_ARTIST, pa.getActionType());
            Assertions.assertTrue(
                    pa.getValue().contains("Evanescence") ||
                            pa.getValue().contains("Pink Floyd") ||
                            pa.getValue().contains("Therapy") ||
                            pa.getValue().contains("Tori Amos") ||
                            pa.getValue().contains("Abigail Williams") ||
                            pa.getValue().contains("Agua De Annique") ||
                            pa.getValue().contains("Christina Aguilera") ||
                            pa.getValue().contains("The Sisters Of Mercy")
            );
            assertThat(processDetail.get(i)).isIn(expectedProcessDetails);
        }
    }

    void testSizeDuration(Artifact artifact, List<Track> tracks, List<MediaFile> mediaFiles, long size) {
        //duration
        assertThat(tracks.stream().collect(Collectors.summarizingLong(Track::getDuration)).getSum()).isEqualTo(
                mediaFiles.stream().collect(Collectors.summarizingLong(MediaFile::getDuration)).getSum()
        );
        assertThat(artifact.getDuration()).isEqualTo(
                tracks.stream().collect(Collectors.summarizingLong(Track::getDuration)).getSum()
        );

        //size
        assertThat(artifact.getSize()).isEqualTo(
                mediaFiles.stream().collect(Collectors.summarizingLong(MediaFile::getSize)).getSum()
        );
        if (size > 0) {
            assertThat(artifact.getSize()).isEqualTo(size);
        }
    }

    void testOneMediaFilePerOneTrack(String artistName, String artifactName, int trackCount, long size) {
        log.info("testOneMediaFilePerOneTrack: artistName=" + artistName + ", artifactName=" + artifactName);
        Artist artist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, artistName).orElseThrow();
        List<Artifact> artifacts = artifactRepository
                .getArtifactsByArtist(artist)
                .stream()
                .filter(a -> a.getTitle().equals(artifactName))
                .collect(Collectors.toList());
        Assertions.assertEquals(1, artifacts.size());

        List<MediaFile> mediaFiles = mediaFileRepository.findAllByArtifact(artifacts.get(0));
        Assertions.assertEquals(trackCount, mediaFiles.size());

        List<Track> tracks = trackRepository.findAllByArtifact(artifacts.get(0));
        Assertions.assertEquals(trackCount, tracks.size());

        testSizeDuration(artifacts.get(0), tracks, mediaFiles, size);
    }

    void testOneMediaFilePerAllTracks(String artistName, String artifactName, int trackCount, int diskCount, long size) {
        Artist artist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, artistName).orElseThrow();
        List<Artifact> artifacts = artifactRepository
                .getArtifactsByArtist(artist)
                .stream()
                .filter(a -> a.getTitle().equals(artifactName))
                .collect(Collectors.toList());
        Assertions.assertEquals(1, artifacts.size());

        List<MediaFile> mediaFiles = mediaFileRepository.findAllByArtifact(artifacts.get(0));
        Assertions.assertEquals(diskCount, mediaFiles.size());

        List<Track> tracks = trackRepository.findAllByArtifact(artifacts.get(0));
        Assertions.assertEquals(trackCount, tracks.size());

        if (size > 0) {
            testSizeDuration(artifacts.get(0), tracks, mediaFiles, size);
        }
    }


    @Test
    @Order(2)
    @Sql({"/schema.sql", "/data.sql"})
    void testOk() {
        List.of(ARTIST_LIST)
                .forEach(s -> artistRepository.save(
                        new EntityArtistBuilder().withType(ArtistType.ARTIST).withName(s).build()
                ));

        processService.executeProcessor(PROCESSOR_TYPE, null);

        ProcessInfo pi = processService.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        int item = 0;
        assertThat(processDetails.get(item++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started LA Loader"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(item++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists loaded"),
                        ProcessingStatus.INFO,
                        8,
                        null)
        );

        assertThat(processDetails.get(item++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts loaded"),
                        ProcessingStatus.INFO,
                        10,
                        null)
        );

        assertThat(processDetails.get(item++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Tracks loaded"),
                        ProcessingStatus.INFO,
                        117,
                        null)
        );

        assertThat(processDetails.get(item)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );

        testOneMediaFilePerOneTrack("Abigail Williams", "In The Absence Of Light", 8, 14016145L);
        testOneMediaFilePerAllTracks("Agua De Annique", "Air", 13, 1, 1854548);
        testOneMediaFilePerOneTrack("Christina Aguilera", "Back To Basics", 22, 22135346 + 13557777);
        testOneMediaFilePerAllTracks("Evanescence", "Origin", 11, 1, 1620441);
        testOneMediaFilePerOneTrack("Evanescence", "Evanescence", 16, 20995495);
        testOneMediaFilePerAllTracks("Pink Floyd", "Delicate Sound Of Thunder", 15, 2, 935874 + 935874);
        testOneMediaFilePerOneTrack("The Sisters Of Mercy", "Anaconda 7 Inch Single", 1, 1745574);
        testOneMediaFilePerAllTracks("Therapy", "Nurse", 10, 1, 2462664);
        testOneMediaFilePerAllTracks("Therapy", "Infernal Love", 11, 1, 2462664);
        testOneMediaFilePerAllTracks("Tori Amos", "Y Kant Tori Read", 10, 1, 13577349);
    }
}