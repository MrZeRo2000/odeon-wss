package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.ArrayList;
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
            "Evanescence", "Pink Floyd", "Therapy", "Tori Amos", "Abigail Williams", "Agua De Annique", "Christina Aguilera"
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
    private CompositionRepository compositionRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testNoArtists() throws Exception {
        List<ProcessDetail> processDetail;

        // warnings - no artists exist
        processService.executeProcessor(PROCESSOR_TYPE, null);
        processDetail = processService.getProcessInfo().getProgressDetails();

        Assertions.assertEquals(10, processDetail.size());
        Assertions.assertEquals(ProcessingStatus.WARNING, processService.getProcessInfo().getProcessingStatus());

        List<ProcessDetail> expectedProcessDetails = Stream.of(ARTIST_LIST)
                .map(v -> new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Artist " + v + " not found", new ArrayList<>()),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, v))
                )
                .collect(Collectors.toList());

        // check processing progress
        for (int i = 1; i < 8; i++) {
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
                            pa.getValue().contains("Christina Aguilera")
            );
            assertThat(processDetail.get(i)).isIn(expectedProcessDetails);
        }
    }

    void testSizeDuration(Artifact artifact, List<Composition> compositions, List<MediaFile> mediaFiles, long size) {
        //duration
        assertThat(compositions.stream().collect(Collectors.summarizingLong(Composition::getDuration)).getSum()).isEqualTo(
                mediaFiles.stream().collect(Collectors.summarizingLong(MediaFile::getDuration)).getSum()
        );
        assertThat(artifact.getDuration()).isEqualTo(
                compositions.stream().collect(Collectors.summarizingLong(Composition::getDuration)).getSum()
        );

        //size
        assertThat(artifact.getSize()).isEqualTo(
                mediaFiles.stream().collect(Collectors.summarizingLong(MediaFile::getSize)).getSum()
        );
        if (size > 0) {
            assertThat(artifact.getSize()).isEqualTo(size);
        }
    }

    void testOneMediaFilePerOneComposition(String artistName, String artifactName, int compositionCount, long size) {
        log.info("testOneMediaFilePerOneComposition: artistName=" + artistName + ", artifactName=" + artifactName);
        Artist artist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, artistName).orElseThrow();
        List<Artifact> artifacts = artifactRepository
                .getArtifactsByArtist(artist)
                .stream()
                .filter(a -> a.getTitle().equals(artifactName))
                .collect(Collectors.toList());
        Assertions.assertEquals(1, artifacts.size());

        List<MediaFile> mediaFiles = mediaFileRepository.findAllByArtifact(artifacts.get(0));
        Assertions.assertEquals(compositionCount, mediaFiles.size());

        List<Composition> compositions = compositionRepository.findAllByArtifact(artifacts.get(0));
        Assertions.assertEquals(compositionCount, compositions.size());

        testSizeDuration(artifacts.get(0), compositions, mediaFiles, size);
    }

    void testOneMediaFilePerAllCompositions(String artistName, String artifactName, int compositionCount, int diskCount, long size) {
        Artist artist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, artistName).orElseThrow();
        List<Artifact> artifacts = artifactRepository
                .getArtifactsByArtist(artist)
                .stream()
                .filter(a -> a.getTitle().equals(artifactName))
                .collect(Collectors.toList());
        Assertions.assertEquals(1, artifacts.size());

        List<MediaFile> mediaFiles = mediaFileRepository.findAllByArtifact(artifacts.get(0));
        Assertions.assertEquals(diskCount, mediaFiles.size());

        List<Composition> compositions = compositionRepository.findAllByArtifact(artifacts.get(0));
        Assertions.assertEquals(compositionCount, compositions.size());

        testSizeDuration(artifacts.get(0), compositions, mediaFiles, size);
    }


    @Test
    @Order(2)
    @Sql({"/schema.sql", "/data.sql"})
    void testOk() throws Exception {
        List.of(ARTIST_LIST)
                .forEach(s -> artistRepository.save(
                        new EntityArtistBuilder().withType(ArtistType.ARTIST).withName(s).build()
                ));

        processService.executeProcessor(PROCESSOR_TYPE, null);

        ProcessInfo pi = processService.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProgressDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        int item = 0;
        assertThat(processDetails.get(item++)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Started LA Loader", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(item++)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Artists loaded", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        7,
                        null)
        );

        assertThat(processDetails.get(item++)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Artifacts loaded", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        9,
                        null)
        );

        assertThat(processDetails.get(item++)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Compositions loaded", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        116,
                        null)
        );

        assertThat(processDetails.get(item)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );

        testOneMediaFilePerOneComposition("Abigail Williams", "In The Absence Of Light", 8, 400492832L);
        testOneMediaFilePerAllCompositions("Agua De Annique", "Air", 13, 1, 317793966);
        testOneMediaFilePerOneComposition("Christina Aguilera", "Back To Basics", 22, 322883508 + 214576534);
        testOneMediaFilePerAllCompositions("Evanescence", "Origin", 11, 1, 324579670);
        testOneMediaFilePerOneComposition("Evanescence", "Evanescence", 16, 466334102);
        testOneMediaFilePerAllCompositions("Pink Floyd", "Delicate Sound Of Thunder", 15, 2, 294597930 + 338702030);
        testOneMediaFilePerAllCompositions("Therapy", "Nurse", 10, 1, 267984588);
        testOneMediaFilePerAllCompositions("Therapy", "Infernal Love", 11, 1, 330812393);
        testOneMediaFilePerAllCompositions("Tori Amos", "Y Kant Tori Read", 10, 1, 286331932);
    }

}