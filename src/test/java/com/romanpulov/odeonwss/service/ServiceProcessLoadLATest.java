package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessLoadLATest {

    private static final Logger log = Logger.getLogger(ServiceProcessLoadLATest.class.getSimpleName());

    @Autowired
    private ProcessService processService;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testNoArtists() throws Exception {
        List<ProgressDetail> progressDetail;

        // warnings - no artists exist
        processService.executeProcessor(ProcessorType.LA_LOADER, null);
        progressDetail = processService.getProcessInfo().getProgressDetails();

        Assertions.assertEquals(6, progressDetail.size());
        Assertions.assertEquals(ProcessingStatus.WARNING, processService.getProcessInfo().getProcessingStatus());

        // check processing progress
        for (int i = 1; i < 5; i++) {
            ProcessingAction pa = progressDetail.get(i).getProcessingAction();
            Assertions.assertNotNull(pa);
            Assertions.assertEquals(ProcessingActionType.ADD_ARTIST, pa.getActionType());
            Assertions.assertTrue(
                    pa.getValue().contains("Evanescence") ||
                            pa.getValue().contains("Pink Floyd") ||
                            pa.getValue().contains("Therapy") ||
                            pa.getValue().contains("Tori Amos")
            );
        }
    }
}
