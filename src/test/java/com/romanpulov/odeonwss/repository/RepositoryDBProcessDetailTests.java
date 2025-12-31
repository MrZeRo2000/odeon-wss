package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
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
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryDBProcessDetailTests {

    @Autowired
    DBProcessInfoRepository dbProcessInfoRepository;

    @Autowired
    DBProcessDetailRepository dbProcessDetailRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testSave() {
        assertThat(
                StreamSupport.stream(dbProcessInfoRepository.findAll().spliterator(), false).count()
        ).isEqualTo(0L);

        assertThat(
                StreamSupport.stream(dbProcessDetailRepository.findAll().spliterator(), false).count()
        ).isEqualTo(0L);

        DBProcessInfo dbProcessInfo = new DBProcessInfo();
        dbProcessInfo.setProcessorType(ProcessorType.MP3_LOADER);
        dbProcessInfo.setProcessingStatus(ProcessingStatus.INFO);
        dbProcessInfo.setUpdateDateTime(LocalDateTime.of(2010, 4, 9, 14, 54, 12));

        dbProcessInfoRepository.save(dbProcessInfo);

        List<DBProcessInfo> dbProcessInfoList = StreamSupport
                .stream(dbProcessInfoRepository.findAll().spliterator(), false)
                .toList();
        assertThat(dbProcessInfoList.size()).isEqualTo(1L);

        DBProcessDetail dbProcessDetail = new DBProcessDetail();
        dbProcessDetail.setDbProcessInfo(dbProcessInfo);
        dbProcessDetail.setProcessingStatus(ProcessingStatus.IN_PROGRESS);
        dbProcessDetail.setMessage("Doing something");
        dbProcessDetail.setRows(22L);
        dbProcessDetail.setUpdateDateTime(LocalDateTime.of(2022, 2, 23, 7, 23, 45));

        dbProcessDetailRepository.save(dbProcessDetail);

        List<DBProcessDetail> dbProcessDetails = StreamSupport
                .stream(dbProcessDetailRepository.findAll().spliterator(), false)
                .toList();
        assertThat(dbProcessDetails.size()).isEqualTo(1L);

        DBProcessDetail savedDBProcessDetail = dbProcessDetails.get(0);
        assertThat(savedDBProcessDetail.getProcessingStatus()).isEqualTo(ProcessingStatus.IN_PROGRESS);
        assertThat(savedDBProcessDetail.getDbProcessInfo().getId()).isEqualTo(dbProcessInfoList.get(0).getId());
        assertThat(savedDBProcessDetail.getMessage()).isEqualTo("Doing something");
        assertThat(savedDBProcessDetail.getRows()).isEqualTo(22L);
        assertThat(savedDBProcessDetail.getUpdateDateTime()).isEqualTo(LocalDateTime.of(2022, 2, 23, 7, 23, 45));
    }
}

