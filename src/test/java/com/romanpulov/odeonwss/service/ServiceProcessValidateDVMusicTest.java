package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled
public class ServiceProcessValidateDVMusicTest {

    private static final Logger log = Logger.getLogger(ServiceProcessValidateDVMusicTest.class.getSimpleName());

    @Autowired
    ProcessService service;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    // @Disabled("For dev purposes")
    void testLoadDV() {
        // load artists
        service.executeProcessor(ProcessorType.ARTISTS_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        log.info("Artist Importer Processing info: " + service.getProcessInfo());

        // load dv music
        service.executeProcessor(ProcessorType.DV_MUSIC_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        log.info("Music Importer Processing info: " + service.getProcessInfo());

        // update dv music media
        service.executeProcessor(ProcessorType.DV_MUSIC_MEDIA_LOADER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        log.info("Music Media Loader Processing info: " + service.getProcessInfo());
    }


    @Test
    @Order(2)
    @Rollback(false)
    void testValidate() {
        service.executeProcessor(ProcessorType.DV_MUSIC_VALIDATOR);

        log.info("Processing info: " + service.getProcessInfo());
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
    }
}
