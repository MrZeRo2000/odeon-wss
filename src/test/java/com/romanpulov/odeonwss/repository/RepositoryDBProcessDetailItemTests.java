package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
import com.romanpulov.odeonwss.entity.DBProcessDetailItem;
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
public class RepositoryDBProcessDetailItemTests {

    @Autowired
    DBProcessInfoRepository dbProcessInfoRepository;

    @Autowired
    DBProcessDetailRepository dbProcessDetailRepository;

    @Autowired
    DBProcessDetailItemRepository dbProcessDetailItemRepository;

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

        assertThat(
                StreamSupport.stream(dbProcessDetailItemRepository.findAll().spliterator(), false).count()
        ).isEqualTo(0L);

        DBProcessInfo dbProcessInfo = new DBProcessInfo();
        dbProcessInfo.setProcessorType(ProcessorType.MP3_LOADER);
        dbProcessInfo.setProcessingStatus(ProcessingStatus.INFO);
        dbProcessInfo.setUpdateDateTime(LocalDateTime.of(2011, 5, 19, 2, 45, 55));

        dbProcessInfoRepository.save(dbProcessInfo);

        List<DBProcessInfo> dbProcessInfoList = StreamSupport
                .stream(dbProcessInfoRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        assertThat(dbProcessInfoList.size()).isEqualTo(1L);

        DBProcessDetail dbProcessDetail = new DBProcessDetail();
        dbProcessDetail.setDbProcessInfo(dbProcessInfo);
        dbProcessDetail.setProcessingStatus(ProcessingStatus.SUCCESS);
        dbProcessDetail.setMessage("Success");
        dbProcessDetail.setRows(4L);
        dbProcessDetail.setUpdateDateTime(LocalDateTime.of(2022, 5, 21, 6, 43, 21));

        dbProcessDetailRepository.save(dbProcessDetail);

        List<DBProcessDetail> dbProcessDetails = StreamSupport
                .stream(dbProcessDetailRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        assertThat(dbProcessDetails.size()).isEqualTo(1L);

        DBProcessDetailItem dbProcessDetailItem1 = new DBProcessDetailItem();
        dbProcessDetailItem1.setDbProcessDetail(dbProcessDetail);
        dbProcessDetailItem1.setValue("Value 1");

        DBProcessDetailItem dbProcessDetailItem2 = new DBProcessDetailItem();
        dbProcessDetailItem2.setDbProcessDetail(dbProcessDetail);
        dbProcessDetailItem2.setValue("Value 2");

        dbProcessDetailItemRepository.saveAll(List.of(dbProcessDetailItem1, dbProcessDetailItem2));

        List<DBProcessDetailItem> savedDBProcessDetailItems = dbProcessDetailItemRepository
                .findAllByDbProcessDetailOrderByIdAsc(dbProcessDetail);
        assertThat(savedDBProcessDetailItems.size()).isEqualTo(2);
        assertThat(savedDBProcessDetailItems.get(0).getValue()).isEqualTo("Value 1");
        assertThat(savedDBProcessDetailItems.get(1).getDbProcessDetail().getId()).isEqualTo(dbProcessDetail.getId());
    }
}

