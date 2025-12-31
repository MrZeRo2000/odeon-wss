package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

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

        assertThrows(Exception.class, () -> processInfoRepository.save(processInfo));

        processInfo.setProcessingStatus(ProcessingStatus.FAILURE);
        processInfo.setLastUpdated(null);

        assertThrows(Exception.class, () -> processInfoRepository.save(processInfo));
    }

    @Test
    @Order(3)
    void testSaveDetailItemsOk() {
        ProcessInfo processInfo = new ProcessInfo(ProcessorType.MP3_LOADER);
        processInfo.addProcessDetails(ProcessDetail.fromInfoMessage("Loaded", 31));
        processInfo.addProcessDetails(ProcessDetail.fromErrorMessage("Error"));
        processInfo.addProcessDetails(ProcessDetail.fromWarningMessageWithAction(
                "Warning", ProcessingActionType.ADD_ARTIST, "MyArtist"));
        processInfo.addProcessDetails(ProcessDetail.fromErrorMessage(
                "Another Error", List.of("One data", "Another data")));

        processInfoRepository.save(processInfo);

        ProcessInfo savedProcessInfo = processInfoRepository.findById(2L).orElseThrow();

        assertThat(savedProcessInfo.getProcessorType()).isEqualTo(ProcessorType.MP3_LOADER);
        assertThat(savedProcessInfo.getProcessDetails().size()).isEqualTo(4);

        assertThat(savedProcessInfo.getProcessDetails().get(0).getInfo().getMessage()).isEqualTo("Loaded");
        assertThat(savedProcessInfo.getProcessDetails().get(0).getRows()).isEqualTo(31L);

        assertThat(savedProcessInfo.getProcessDetails().get(1).getStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(savedProcessInfo.getProcessDetails().get(1).getInfo().getMessage()).isEqualTo("Error");
        assertThat(savedProcessInfo.getProcessDetails().get(1).getRows()).isNull();
        assertThat(savedProcessInfo.getProcessDetails().get(1).getProcessingAction()).isNull();

        assertThat(savedProcessInfo.getProcessDetails().get(2).getStatus()).isEqualTo(ProcessingStatus.WARNING);
        assertThat(savedProcessInfo.getProcessDetails().get(2).getInfo().getMessage()).isEqualTo("Warning");
        assertThat(savedProcessInfo.getProcessDetails().get(2).getProcessingAction().getActionType()).isEqualTo(
                ProcessingActionType.ADD_ARTIST);
        assertThat(savedProcessInfo.getProcessDetails().get(2).getProcessingAction().getValue()).isEqualTo("MyArtist");

        assertThat(savedProcessInfo.getProcessDetails().get(3).getStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(savedProcessInfo.getProcessDetails().get(3).getInfo().getItems().get(0)).isEqualTo("One data");
        assertThat(savedProcessInfo.getProcessDetails().get(3).getInfo().getItems().get(1)).isEqualTo("Another data");
    }
}
