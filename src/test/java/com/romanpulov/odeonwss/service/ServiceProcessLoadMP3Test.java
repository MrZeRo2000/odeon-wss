package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessLoadMP3Test {

    private static final Logger log = Logger.getLogger(ServiceProcessLoadMP3Test.class.getSimpleName());
    public static final ProcessorType PROCESSOR_TYPE = ProcessorType.MP3_LOADER;

    @Autowired
    ProcessService service;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    CompositionRepository compositionRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;


    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testOk() {
        Arrays.asList("Aerosmith", "Kosheen").forEach(s ->
                artistRepository.save(
                        new EntityArtistBuilder()
                                .withType(ArtistType.ARTIST)
                                .withName(s)
                                .build()
                ));

        log.info("Created artists");
        Assertions.assertDoesNotThrow(() -> service.executeProcessor(PROCESSOR_TYPE));

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        assertThat(pi.getProgressDetails().get(0)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Started MP3 Loader", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProgressDetails().get(1)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists loaded", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        2,
                        null)
        );


        assertThat(pi.getProgressDetails().get(2)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifacts loaded", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        3,
                        null)
        );

        assertThat(pi.getProgressDetails().get(3)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Compositions loaded", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        40,
                        null)
        );

        assertThat(pi.getProgressDetails().get(4)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );

        Artist aerosmithArtist = artistRepository.findFirstByName("Aerosmith").orElseThrow();
        Artist kosheenArtist = artistRepository.findFirstByName("Kosheen").orElseThrow();

        Artifact honkinArtifact = artifactRepository.getArtifactsByArtist(aerosmithArtist).get(0);
        Assertions.assertEquals(compositionRepository.findAllByArtifact(honkinArtifact).size(), mediaFileRepository.findAllByArtifact(honkinArtifact).size());
    }

    @Test
    @Order(2)
    @Sql({"/schema.sql", "/data.sql"})
    void testNoArtistExistsShouldFail() throws Exception {
        List<ProgressDetail> progressDetail;

        // warnings - no artists exist
        service.executeProcessor(PROCESSOR_TYPE, null);

        ProcessInfo pi = service.getProcessInfo();
        progressDetail = pi.getProgressDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.WARNING);

        assertThat(pi.getProgressDetails().get(1)).isIn(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artist Aerosmith not found", new ArrayList<>()),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Aerosmith")),
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artist Kosheen not found", new ArrayList<>()),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Kosheen"))
        );

        assertThat(pi.getProgressDetails().get(2)).isIn(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artist Aerosmith not found", new ArrayList<>()),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Aerosmith")),
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artist Kosheen not found", new ArrayList<>()),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Kosheen"))
        );

        assertThat(pi.getProgressDetails().get(3)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists loaded", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        0,
                        null)
        );

        assertThat(pi.getProgressDetails().get(4)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.WARNING,
                        null,
                        null)
        );


        // check processing progress
        ProcessingAction pa = progressDetail.get(1).getProcessingAction();
        Assertions.assertNotNull(pa);
        Assertions.assertEquals(ProcessingActionType.ADD_ARTIST, pa.getActionType());
        Assertions.assertTrue(pa.getValue().contains("Aerosmith") || pa.getValue().contains("Kosheen"));

    }

    @Test
    @Order(3)
    @Sql({"/schema.sql", "/data.sql"})
    void testNoPathExistsShouldFail() throws Exception {
        List<ProgressDetail> progressDetail;

        // error - path not exist
        service.executeProcessor(PROCESSOR_TYPE, "non_existing_path");
        progressDetail = service.getProcessInfo().getProgressDetails();
        Assertions.assertEquals(3, progressDetail.size());
        Assertions.assertEquals(ProcessingStatus.FAILURE, progressDetail.get(service.getProcessInfo().getProgressDetails().size() - 1).getStatus());
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(4)
    void testDirectoryWithFiles() throws Exception {
        service.executeProcessor(PROCESSOR_TYPE, "");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().getMessage().contains("directory, found:")));
    }

    @Test
    @Order(5)
    void testWrongArtifactTitle() throws Exception {
        Artist artist = new Artist();
        artist.setType(ArtistType.ARTIST);
        artist.setName("Aerosmith");

        artistRepository.save(artist);

        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/wrong_artifact_title/");
        ProcessInfo pi = service.getProcessInfo();
        List<ProgressDetail> progressDetail = pi.getProgressDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(progressDetail.get(1)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists loaded", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        1,
                        null)
        );

        assertThat(progressDetail.get(2)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Error parsing artifact name",
                                List.of("Aerosmith >> 2004  Honkin'On Bobo")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(pi.getProgressDetails().get(3)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifacts loaded", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        0,
                        null)
        );

        assertThat(pi.getProgressDetails().get(4)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        //Assertions.assertTrue(service.getProcessInfo().getProgressDetails().get(service.getProcessInfo().getProgressDetails().size() - 2).getInfo().getMessage().contains("Error parsing artifact name"));
    }

    @Test
    @Order(6)
    @Sql({"/schema.sql", "/data.sql"})
    void testOneArtistNotExists() {
        artistRepository.save(
                new EntityArtistBuilder()
                        .withType(ArtistType.ARTIST)
                        .withName("Kosheen")
                        .build()
        );
        log.info("Created artist");
        Assertions.assertDoesNotThrow(() -> service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/ok/MP3 Music/"));
        Assertions.assertEquals(ProcessingStatus.WARNING, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(7)
    @Sql({"/schema.sql", "/data.sql"})
    void testNoArtistResolving() {
        List<ProgressDetail> progressDetail;

        service.executeProcessor(PROCESSOR_TYPE, null);
        progressDetail = service.getProcessInfo().getProgressDetails();
        Assertions.assertEquals(5, progressDetail.size());
        Assertions.assertEquals(ProcessingStatus.WARNING, service.getProcessInfo().getProcessingStatus());

        // find first action
        ProcessingAction processingAction = progressDetail.get(1).getProcessingAction();
        Assertions.assertNotNull(processingAction);

        // resolve first action
        service.getProcessInfo().resolveAction(processingAction);
        Assertions.assertEquals(4, progressDetail.size());
        Assertions.assertEquals(ProcessingStatus.WARNING,
                service.getProcessInfo().getProgressDetails().get(service.getProcessInfo().getProgressDetails().size() - 1).getStatus());

        // find second action
        processingAction = progressDetail.get(1).getProcessingAction();
        Assertions.assertNotNull(processingAction);

        // resolve second action
        service.getProcessInfo().resolveAction(processingAction);
        Assertions.assertEquals(3, progressDetail.size());
    }

}
