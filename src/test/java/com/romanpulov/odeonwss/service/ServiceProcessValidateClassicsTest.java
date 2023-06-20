package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateClassicsTest {

    private static final Logger log = Logger.getLogger(ServiceProcessValidateClassicsTest.class.getName());

    @Autowired
    ProcessService service;

    @Autowired
    ArtifactRepository artifactRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testLoad() {
        service.executeProcessor(ProcessorType.CLASSICS_IMPORTER);

        log.info("Processing info: " + service.getProcessInfo());
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(2)
    void testValidateOk() {
        service.executeProcessor(ProcessorType.CLASSICS_VALIDATOR);

        log.info("Processing info: " + service.getProcessInfo());
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(3)
    void testFail() {
        Artifact artifact = artifactRepository.findById(1L).orElseThrow();
        artifact.setTitle(artifact.getTitle() + "(changed)");
        artifactRepository.save(artifact);

        service.executeProcessor(ProcessorType.CLASSICS_VALIDATOR);

        log.info("Processing info: " + service.getProcessInfo());
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
    }
}
