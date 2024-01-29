package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityDBProcessInfoBuilder;
import com.romanpulov.odeonwss.entity.DBProcessInfo;
import com.romanpulov.odeonwss.service.ProcessService;
import com.romanpulov.odeonwss.service.processor.model.ProcessingAction;
import com.romanpulov.odeonwss.service.processor.model.ProcessingActionType;
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
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryDBProcessInfoTests {

    @Autowired
    DBProcessInfoRepository dbProcessInfoRepository;

    @Autowired
    private ProcessService processService;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testPrepare() {
        dbProcessInfoRepository.save(new EntityDBProcessInfoBuilder()
                .withProcessorType(ProcessorType.MP3_LOADER)
                .withProcessingStatus(ProcessingStatus.INFO)
                .withUpdateDateTime(LocalDateTime.of(2022, 2, 12, 18, 45, 22))
                .build()
        );
        dbProcessInfoRepository.save(new EntityDBProcessInfoBuilder()
                .withProcessorType(ProcessorType.DV_MOVIES_VALIDATOR)
                .withProcessingStatus(ProcessingStatus.FAILURE)
                .withUpdateDateTime(LocalDateTime.of(2023, 4, 7, 15, 9, 7))
                .build()
        );
        dbProcessInfoRepository.save(new EntityDBProcessInfoBuilder()
                .withProcessorType(ProcessorType.LA_LOADER)
                .withProcessingStatus(ProcessingStatus.WARNING)
                .withUpdateDateTime(LocalDateTime.of(2023, 7, 2, 10, 6, 45))
                .build()
        );

        processService.executeProcessor(ProcessorType.LA_LOADER, null);
    }

    @Test
    @Order(2)
    void testValidateInsertedDataShouldBeOk() {
        List<DBProcessInfo> dbProcessInfoList = StreamSupport
                .stream(dbProcessInfoRepository.findAll().spliterator(), false)
                .toList();
        assertThat(dbProcessInfoList.size()).isEqualTo(4L);

        DBProcessInfo savedDBProcessInfo = dbProcessInfoList
                .stream()
                .filter(p -> p.getId().equals(1L))
                .findFirst()
                .orElseThrow();
        assertThat(savedDBProcessInfo.getId()).isEqualTo(1L);
        assertThat(savedDBProcessInfo.getProcessorType()).isEqualTo(ProcessorType.MP3_LOADER);
        assertThat(savedDBProcessInfo.getProcessingStatus()).isEqualTo(ProcessingStatus.INFO);
        assertThat(savedDBProcessInfo.getUpdateDateTime())
                .isEqualTo(LocalDateTime.of(2022, 2, 12, 18, 45, 22));

    }

    @Test
    @Order(2)
    void testFindAllOrderedByUpdateDateTime() {
        var dbProcessList = dbProcessInfoRepository.findAllOrderedByUpdateDateTime();
        assertThat(dbProcessList.size()).isEqualTo(4);

        assertThat(dbProcessList.get(1).getId()).isEqualTo(3L);
        assertThat(dbProcessList.get(1).getProcessingStatus()).isEqualTo(ProcessingStatus.WARNING);
        assertThat(dbProcessList.get(1).getProcessorType()).isEqualTo(ProcessorType.LA_LOADER);

        assertThat(dbProcessList.get(2).getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(dbProcessList.get(2).getProcessorType()).isEqualTo(ProcessorType.DV_MOVIES_VALIDATOR);
        assertThat(dbProcessList.get(2).getUpdateDateTime())
                .isEqualTo(LocalDateTime.of(2023, 4, 7, 15, 9, 7));
    }

    @Test
    @Order(2)
    void testFindByIdWithDetails() {
        var data_1 = dbProcessInfoRepository.findFlatDTOByIdWithDetails(1L);
        assertThat(data_1.get(0).getId()).isEqualTo(1L);

        var data_4 = dbProcessInfoRepository.findFlatDTOByIdWithDetails(4L);
        assertThat(data_4.get(0).getId()).isEqualTo(4L);
        assertThat(data_4.get(0).getDetailMessage()).isEqualTo("Started LA Loader");
        assertThat(data_4.get(0).getProcessingActionType()).isNull();
        assertThat(data_4.get(0).getProcessingActionValue()).isNull();

        assertThat(data_4.get(1).getDetailProcessingStatus()).isEqualTo(ProcessingStatus.WARNING);
        assertThat(data_4.get(1).getDetailMessage()).contains("not found");
        assertThat(data_4.get(1).getProcessingActionType()).isEqualTo(ProcessingActionType.ADD_ARTIST);
        assertThat(data_4.get(1).getProcessingActionValue()).isNotNull();
    }
}

