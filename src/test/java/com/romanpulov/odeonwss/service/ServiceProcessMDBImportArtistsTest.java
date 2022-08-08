package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Disabled
public class ServiceProcessMDBImportArtistsTest {

    private static final Logger log = Logger.getLogger(ServiceProcessMDBImportArtistsTest.class.getSimpleName());

    @Autowired
    ProcessService service;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testLoad() {
        service.executeProcessor(ProcessorType.ARTISTS_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().contains("Artists imported")));
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().contains("Artist details imported")));
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().contains("Artist categories imported")));
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().contains("Artist lyrics imported")));
        log.info("Processing info: " + service.getProcessInfo());
    }
}
