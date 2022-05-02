package com.romanpulov.odeonwss;

import com.romanpulov.odeonwss.service.ProcessService;
import com.romanpulov.odeonwss.service.processor.*;
import org.apache.tomcat.jni.Proc;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Sql({"/schema.sql", "/data.sql"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessLoadMP3Test {

    @Autowired
    ProcessService service;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void test() throws Exception {
        List<ProgressInfo> progressInfo;

        // warnings - no artists exist
        service.executeProcessor(ProcessorType.MP3_LOADER, null);
        progressInfo = service.getProgress();
        Assertions.assertEquals(3, progressInfo.size());
        Assertions.assertEquals(ProcessingStatus.WARNING, service.getLastProcessingStatus());
        Assertions.assertEquals(ProcessingStatus.WARNING, service.getFinalProgressInfo().getStatus());

        // error - path not exist
        service.executeProcessor(ProcessorType.MP3_LOADER, "non_existing_path");
        progressInfo = service.getProgress();
        Assertions.assertEquals(1, progressInfo.size());
        Assertions.assertEquals(ProcessingStatus.FAILURE, progressInfo.get(service.getProgress().size() - 1).getStatus());
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getLastProcessingStatus());

        // warning - no artists exist
        service.executeProcessor(ProcessorType.MP3_LOADER, null);
        progressInfo = service.getProgress();
        Assertions.assertEquals(3, progressInfo.size());
        Assertions.assertEquals(ProcessingStatus.WARNING, service.getLastProcessingStatus());

        ProcessingAction pa = progressInfo.get(0).getProcessingAction();
        Assertions.assertNotNull(pa);
        Assertions.assertEquals(ProcessingActionType.ADD_ARTIST, pa.getActionType());
        Assertions.assertTrue(pa.getValue().contains("Aerosmith") || pa.getValue().contains("Kosheen"));
    }

    @Test
    @Order(2)
    void testDirectoryWithFiles() throws Exception {
        service.executeProcessor(ProcessorType.MP3_LOADER, "");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getLastProcessingStatus());
        Assertions.assertTrue(service.getProgress().stream().anyMatch(p -> p.getInfo().contains("directory, found ")));
    }
}
