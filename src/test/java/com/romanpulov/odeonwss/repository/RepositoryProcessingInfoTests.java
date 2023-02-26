package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.service.processor.model.ProcessDetail;
import com.romanpulov.odeonwss.service.processor.model.ProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryProcessingInfoTests {
    @Autowired
    private ProcessInfoRepository processInfoRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testSaveOk() {
        ProcessInfo processInfo = new ProcessInfo(ProcessorType.LA_LOADER);
        processInfo.setProcessingStatus(ProcessingStatus.SUCCESS);
        processInfo.setLastUpdated(LocalDateTime.of(2022, 10, 4, 10, 12, 44));

        //ProcessDetail processDetail = new ProcessDetail()
    }
}
