package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled
public class ServiceProcessMDBImportClassicsTest {

    private static final Logger log = Logger.getLogger(ServiceProcessMDBImportClassicsTest.class.getSimpleName());

    @Autowired
    ProcessService service;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    CompositionRepository compositionRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testLoad() {
        service.executeProcessor(ProcessorType.CLASSICS_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        log.info("Processing info: " + service.getProcessInfo());
    }

    @Test
    @Order(2)
    void testValidate() {
        Assertions.assertEquals(0, artistRepository.getAllByType(ArtistType.ARTIST).size());

        List<Artist> artists = artistRepository.getAllByType(ArtistType.CLASSICS);
        Assertions.assertTrue(artists.size() > 0);
        Assertions.assertEquals(artists.size(), artists.stream().map(Artist::getMigrationId).collect(Collectors.toSet()).size());

        Assertions.assertEquals(99, StreamSupport.stream(artistRepository.findAll().spliterator(), false).count());
        Assertions.assertEquals(73, StreamSupport.stream(artifactRepository.findAll().spliterator(), false).count());
        Assertions.assertEquals(157, StreamSupport.stream(compositionRepository.findAll().spliterator(), false).count());
    }

    @Test
    @Order(3)
    void testRunSecondTime() {
        long artistCount = StreamSupport.stream(artistRepository.findAll().spliterator(), false).count();
        long artifactCount = StreamSupport.stream(artifactRepository.findAll().spliterator(), false).count();
        long compositionCount = StreamSupport.stream(compositionRepository.findAll().spliterator(), false).count();

        service.executeProcessor(ProcessorType.CLASSICS_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        Assertions.assertEquals(artistCount, StreamSupport.stream(artistRepository.findAll().spliterator(), false).count());
        Assertions.assertEquals(artifactCount, StreamSupport.stream(artifactRepository.findAll().spliterator(), false).count());
        Assertions.assertEquals(compositionCount, StreamSupport.stream(compositionRepository.findAll().spliterator(), false).count());
    }
}
