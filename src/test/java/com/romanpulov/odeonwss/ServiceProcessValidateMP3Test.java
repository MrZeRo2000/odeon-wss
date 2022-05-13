package com.romanpulov.odeonwss;

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

    @Autowired
    ProcessService service;

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
    }

    @Test
    @Order(3)
    void testMissingFileArtist() throws Exception {
        service.executeProcessor(ProcessorType.MP3_VALIDATOR, "D:/Temp/wrong_artifact_title/");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getLastProcessingStatus());
        Assertions.assertTrue(service.getLastProgressInfo().getInfo().contains("Artists not in files"));
    }
}
