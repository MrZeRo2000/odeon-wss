package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateMP3Test {

    @Autowired
    ArtistRepository artistRepository;

    ProcessService service;

    @Autowired
    public ServiceProcessValidateMP3Test(ProcessService service) {
        this.service = service;
    }


    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql"})
    void testLoadEmptyShouldFail() {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR);
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(2)
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql"})
    void testLoad() {
        service.executeProcessor(ProcessorType.MP3_LOADER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        Assertions.assertEquals(2, artistRepository.getAllByType(ArtistType.ARTIST).size());
    }

    @Test
    @Order(3)
    void testOk() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().contains("Artists validated")));
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().contains("Artifacts validated")));
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().contains("Compositions validated")));
    }

    @Test
    @Order(4)
    void testWrongTitleMissingFileArtist() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR, "../odeon-test-data/wrong_artifact_title/");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
        Assertions.assertTrue(service.getProcessInfo().getLastProgressDetail().getInfo().contains("Artists not in files"));
    }

    @Test
    @Order(5)
    void testMissingFileArtist() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR, "../odeon-test-data/validation_mp3_missing_artist/");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
        Assertions.assertTrue(service.getProcessInfo().getLastProgressDetail().getInfo().contains("Artists not in files"));
    }

    @Test
    @Order(6)
    void testAdditionalFileArtist() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR, "../odeon-test-data/validation_mp3_additional_artist/");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
        Assertions.assertTrue(service.getProcessInfo().getLastProgressDetail().getInfo().contains("Artists not in database"));
    }

    @Test
    @Order(7)
    void testMissingFileArtifact() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR, "../odeon-test-data/validation_mp3_missing_artifact/");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
        Assertions.assertTrue(service.getProcessInfo().getLastProgressDetail().getInfo().contains("Artifacts not in files"));
    }

    @Test
    @Order(8)
    void testMissingFileCompositions() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR, "../odeon-test-data/validation_mp3_missing_compositions/");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
        Assertions.assertTrue(service.getProcessInfo().getLastProgressDetail().getInfo().contains("Compositions not in files"));
    }
}
