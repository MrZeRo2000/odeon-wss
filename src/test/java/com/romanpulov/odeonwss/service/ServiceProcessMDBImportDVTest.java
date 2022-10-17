package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessMDBImportDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessMDBImportClassicsTest.class.getSimpleName());

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    CompositionRepository compositionRepository;

    @Autowired
    ProcessService service;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    //@Disabled("For dev purposes")
    void testLoadArtists() {
        service.executeProcessor(ProcessorType.ARTISTS_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        log.info("Processing info: " + service.getProcessInfo());
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testLoadDVMusic() {
        service.executeProcessor(ProcessorType.DV_MUSIC_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        log.info("Processing info: " + service.getProcessInfo());
    }

    @Test
    @Order(3)
    @Rollback(false)
    void testLoadDVMusicTwo() {
        int oldArtifactsCount = artifactRepository.getAllByArtifactType(ArtifactType.withDVMusic()).size();
        Assertions.assertTrue(oldArtifactsCount > 0);

        long oldCompositionsCount = StreamSupport.stream(compositionRepository.findAll().spliterator(), false).count();
        Assertions.assertTrue(oldCompositionsCount > 0);

        service.executeProcessor(ProcessorType.DV_MUSIC_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        log.info("Processing info: " + service.getProcessInfo());

        int newArtifactsCount = artifactRepository.getAllByArtifactType(ArtifactType.withDVMusic()).size();
        Assertions.assertTrue(newArtifactsCount > 0);
        Assertions.assertEquals(oldArtifactsCount, newArtifactsCount);

        long newCompositionsCount = StreamSupport.stream(compositionRepository.findAll().spliterator(), false).count();
        Assertions.assertTrue(newCompositionsCount > 0);
        Assertions.assertEquals(oldCompositionsCount, newCompositionsCount);
    }
}
