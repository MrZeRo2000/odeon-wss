package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessInfo;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryDBProcessInfoTests {

    @Autowired
    DBProcessInfoRepository dbProcessInfoRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testSave() {
        assertThat(
                StreamSupport.stream(dbProcessInfoRepository.findAll().spliterator(), false).count()
        ).isEqualTo(0L);

        DBProcessInfo dbProcessInfo = new DBProcessInfo();
        dbProcessInfo.setProcessorType(ProcessorType.MP3_LOADER);
        dbProcessInfo.setProcessingStatus(ProcessingStatus.INFO);
        dbProcessInfo.setUpdateDateTime(LocalDateTime.of(2022, 2, 12, 18, 45, 22));

        dbProcessInfoRepository.save(dbProcessInfo);

        List<DBProcessInfo> dbProcessInfoList = StreamSupport
                .stream(dbProcessInfoRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        assertThat(dbProcessInfoList.size()).isEqualTo(1L);

        DBProcessInfo savedDBProcessInfo = dbProcessInfoList.get(0);
        assertThat(savedDBProcessInfo.getId()).isEqualTo(1L);
        assertThat(savedDBProcessInfo.getProcessorType()).isEqualTo(ProcessorType.MP3_LOADER);
        assertThat(savedDBProcessInfo.getProcessingStatus()).isEqualTo(ProcessingStatus.INFO);
        assertThat(savedDBProcessInfo.getUpdateDateTime()).isEqualTo(LocalDateTime.of(2022, 2, 12, 18, 45, 22));
    }
}

