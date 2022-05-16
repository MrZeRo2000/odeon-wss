package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistTypes;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.ProcessService;
import com.romanpulov.odeonwss.service.processor.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.ProcessorType;
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
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql", "/main_artifacts.sql", "/main_compositions.sql", "/main_media_files.sql"})
    void testLoad() {
        Assertions.assertEquals(2, artistRepository.getAllByType(ArtistTypes.A.name()).size());
    }

    @Test
    @Order(2)
    void testOk() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getLastProcessingStatus());
        Assertions.assertTrue(service.getProgress().stream().anyMatch(p -> p.getInfo().contains("Artists validated")));
        Assertions.assertTrue(service.getProgress().stream().anyMatch(p -> p.getInfo().contains("Artifacts validated")));
        Assertions.assertTrue(service.getProgress().stream().anyMatch(p -> p.getInfo().contains("Compositions validated")));
    }

    @Test
    @Order(3)
    void testWrongTitleMissingFileArtist() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR, "D:/Temp/wrong_artifact_title/");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getLastProcessingStatus());
        Assertions.assertTrue(service.getLastProgressInfo().getInfo().contains("Artists not in files"));
    }

    @Test
    @Order(4)
    void testMissingFileArtist() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR, "D:/Temp/validation_mp3_missing_artist/");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getLastProcessingStatus());
        Assertions.assertTrue(service.getLastProgressInfo().getInfo().contains("Artists not in files"));
    }

    @Test
    @Order(5)
    void testAdditionalFileArtist() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR, "D:/Temp/validation_mp3_additional_artist/");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getLastProcessingStatus());
        Assertions.assertTrue(service.getLastProgressInfo().getInfo().contains("Artists not in database"));
    }

    @Test
    @Order(6)
    void testMissingFileArtifact() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR, "D:/Temp/validation_mp3_missing_artifact/");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getLastProcessingStatus());
        Assertions.assertTrue(service.getLastProgressInfo().getInfo().contains("Artifacts not in files"));
    }

    @Test
    @Order(7)
    void testMissingFileCompositions() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR, "D:/Temp/validation_mp3_missing_compositions/");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getLastProcessingStatus());
        Assertions.assertTrue(service.getLastProgressInfo().getInfo().contains("Compositions not in files"));
    }
}
