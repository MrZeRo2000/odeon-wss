package com.romanpulov.odeonwss;

import com.romanpulov.odeonwss.service.ProcessService;
import com.romanpulov.odeonwss.service.processor.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.ProcessorType;
import com.romanpulov.odeonwss.service.processor.ProgressInfo;
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
    void test() throws Exception {
        List<ProgressInfo> progressInfo;

        service.executeProcessor(ProcessorType.MP3_LOADER, null);
        progressInfo = service.getProgress();
        Assertions.assertEquals(1, progressInfo.size());
        Assertions.assertEquals(ProcessingStatus.SUCCESS, progressInfo.get(service.getProgress().size() - 1).status);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getLastProcessingStatus());

        service.executeProcessor(ProcessorType.MP3_LOADER, "non_existing_path");
        progressInfo = service.getProgress();
        Assertions.assertEquals(1, progressInfo.size());
        Assertions.assertEquals(ProcessingStatus.FAILURE, progressInfo.get(service.getProgress().size() - 1).status);
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getLastProcessingStatus());

        service.executeProcessor(ProcessorType.MP3_LOADER, null);
        progressInfo = service.getProgress();
        Assertions.assertEquals(1, progressInfo.size());
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getLastProcessingStatus());
    }

    @Test
    @Order(2)
    void testDirectoryWithFiles() throws Exception {
        service.executeProcessor(ProcessorType.MP3_LOADER, "");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getLastProcessingStatus());
        Assertions.assertTrue(service.getLastProgressInfo().info.contains("directory, found "));
    }
}
