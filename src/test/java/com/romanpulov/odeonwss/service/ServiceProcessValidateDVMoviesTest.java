package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.service.processor.model.ProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import com.romanpulov.odeonwss.service.processor.model.ProgressDetail;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIf(value = "${full.tests.disabled}", loadContext = true)
public class ServiceProcessValidateDVMoviesTest {
    private static final Logger log = Logger.getLogger(ServiceProcessValidateDVMoviesTest.class.getSimpleName());

    @Autowired
    ProcessService service;

    @Autowired
    private AppConfiguration appConfiguration;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepare() {
        DbManagerService.loadOrPrepare(appConfiguration, DbManagerService.DbType.DB_LOADED_MOVIES, () -> {
            service.executeProcessor(ProcessorType.DV_MOVIES_LOADER);
            Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
            log.info("Movies Importer Processing info: " + service.getProcessInfo());
        });
    }

    @Test
    @Order(2)
    void testValidateOk() {
        service.executeProcessor(ProcessorType.DV_MOVIES_VALIDATOR);
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
    }

    @Test
    @Order(3)
    void testContainsFoldersShouldFail() {
        service.executeProcessor(ProcessorType.DV_MOVIES_VALIDATOR, "../odeon-test-data/ok/MP3 Music/");
        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProgressDetails().get(0)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Started Movies Validator", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(pi.getProgressDetails().get(1)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Expected file, found: ..\\odeon-test-data\\ok\\MP3 Music\\Aerosmith\\2004 Honkin'On Bobo",
                                new ArrayList<>()),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(pi.getProgressDetails().get(2)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artifacts not in files",
                                List.of("Крепкий орешек", "Лицензия на убийство", "Обыкновенное чудо")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(pi.getProgressDetails().get(3)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Task status",
                                List.of()),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

    }

}
