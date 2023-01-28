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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessLoadMP3Test {

    private static final Logger log = Logger.getLogger(ServiceProcessLoadMP3Test.class.getSimpleName());

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
    void test() throws Exception {
        List<ProgressDetail> progressDetail;

        // warnings - no artists exist
        service.executeProcessor(ProcessorType.MP3_LOADER, null);
        progressDetail = service.getProcessInfo().getProgressDetails();
        Assertions.assertEquals(4, progressDetail.size());
        Assertions.assertEquals(ProcessingStatus.WARNING, service.getProcessInfo().getProcessingStatus());
        // check processing progress
        ProcessingAction pa = progressDetail.get(1).getProcessingAction();
        Assertions.assertNotNull(pa);
        Assertions.assertEquals(ProcessingActionType.ADD_ARTIST, pa.getActionType());
        Assertions.assertTrue(pa.getValue().contains("Aerosmith") || pa.getValue().contains("Kosheen"));

        // error - path not exist
        service.executeProcessor(ProcessorType.MP3_LOADER, "non_existing_path");
        progressDetail = service.getProcessInfo().getProgressDetails();
        Assertions.assertEquals(3, progressDetail.size());
        Assertions.assertEquals(ProcessingStatus.FAILURE, progressDetail.get(service.getProcessInfo().getProgressDetails().size() - 1).getStatus());
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(2)
    void testDirectoryWithFiles() throws Exception {
        service.executeProcessor(ProcessorType.MP3_LOADER, "");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().getMessage().contains("directory, found:")));
    }

    @Test
    @Order(3)
    void testWrongArtifactTitle() throws Exception {
        Artist artist = new Artist();
        artist.setType(ArtistType.ARTIST);
        artist.setName("Aerosmith");

        artistRepository.save(artist);

        service.executeProcessor(ProcessorType.MP3_LOADER, "../odeon-test-data/wrong_artifact_title/");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().get(service.getProcessInfo().getProgressDetails().size() - 2).getInfo().getMessage().contains("Error parsing artifact name"));
    }

    @Test
    @Order(4)
    @Sql({"/schema.sql", "/data.sql"})
    void testOneArtistNotExists() {
        artistRepository.save(
                new EntityArtistBuilder()
                        .withType(ArtistType.ARTIST)
                        .withName("Kosheen")
                        .build()
        );
        log.info("Created artist");
        Assertions.assertDoesNotThrow(() -> service.executeProcessor(ProcessorType.MP3_LOADER, "../odeon-test-data/ok/MP3 Music/"));
        Assertions.assertEquals(ProcessingStatus.WARNING, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(5)
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
        Assertions.assertDoesNotThrow(() -> service.executeProcessor(ProcessorType.MP3_LOADER, "../odeon-test-data/ok/MP3 Music/"));
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        Artist aerosmithArtist = artistRepository.findFirstByName("Aerosmith").orElseThrow();
        Artist kosheenArtist = artistRepository.findFirstByName("Kosheen").orElseThrow();

        Artifact honkinArtifact = artifactRepository.getArtifactsByArtist(aerosmithArtist).get(0);
        Assertions.assertEquals(compositionRepository.findAllByArtifact(honkinArtifact).size(), mediaFileRepository.findAllByArtifact(honkinArtifact).size());
    }

    @Test
    @Order(6)
    @Sql({"/schema.sql", "/data.sql"})
    void testNoArtistResolving() {
        List<ProgressDetail> progressDetail;

        service.executeProcessor(ProcessorType.MP3_LOADER, null);
        progressDetail = service.getProcessInfo().getProgressDetails();
        Assertions.assertEquals(4, progressDetail.size());
        Assertions.assertEquals(ProcessingStatus.WARNING, service.getProcessInfo().getProcessingStatus());

        Assertions.assertEquals(4, progressDetail.size());

        // find first action
        ProcessingAction processingAction = progressDetail.get(1).getProcessingAction();
        Assertions.assertNotNull(processingAction);

        // resolve first action
        service.getProcessInfo().resolveAction(processingAction);
        Assertions.assertEquals(3, progressDetail.size());
        Assertions.assertEquals(ProcessingStatus.WARNING,
                service.getProcessInfo().getProgressDetails().get(service.getProcessInfo().getProgressDetails().size() - 1).getStatus());

        // find second action
        processingAction = progressDetail.get(1).getProcessingAction();
        Assertions.assertNotNull(processingAction);

        // resolve second action
        service.getProcessInfo().resolveAction(processingAction);
        Assertions.assertEquals(2, progressDetail.size());
    }

}
