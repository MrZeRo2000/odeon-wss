package com.romanpulov.odeonwss.repository;

import com.healthmarketscience.jackcess.ConstraintViolationException;
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

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryProcessingInfoTests {
    @Autowired
    private ProcessInfoRepository processInfoRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testSaveProcessInfoOk() {
        ProcessInfo processInfo = new ProcessInfo(ProcessorType.LA_LOADER);
        processInfo.setProcessingStatus(ProcessingStatus.SUCCESS);
        processInfo.setLastUpdated(LocalDateTime.of(2022, 10, 4, 10, 12, 44));

        processInfoRepository.save(processInfo);

        ProcessInfo savedProcessInfo = processInfoRepository.findById(1L).orElseThrow();
        assertThat(savedProcessInfo.getProcessorType()).isEqualTo(ProcessorType.LA_LOADER);
        assertThat(savedProcessInfo.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        assertThat(savedProcessInfo.getLastUpdated()).isEqualTo(LocalDateTime.of(2022, 10, 4, 10, 12, 44));
    }

    @Test
    @Order(2)
    void testSaveProcessInfoNoProcessingStatusNoUpdateDateShouldFail() {
        ProcessInfo processInfo = new ProcessInfo(ProcessorType.LA_VALIDATOR);
        processInfo.setProcessingStatus(null);

        assertThrows(Exception.class, () -> {
            processInfoRepository.save(processInfo);
        });

        processInfo.setProcessingStatus(ProcessingStatus.FAILURE);
        processInfo.setLastUpdated(null);

        assertThrows(Exception.class, () -> {
            processInfoRepository.save(processInfo);
        });
    }
}
