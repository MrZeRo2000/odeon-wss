package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
import com.romanpulov.odeonwss.entity.DBProcessDetailAction;
import com.romanpulov.odeonwss.entity.DBProcessInfo;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryDBProcessDetailActionTests {

    @Autowired
    DBProcessInfoRepository dbProcessInfoRepository;

    @Autowired
    DBProcessDetailRepository dbProcessDetailRepository;

    @Autowired
    DBProcessDetailActionRepository dbProcessDetailActionRepository;

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
                StreamSupport.stream(dbProcessDetailActionRepository.findAll().spliterator(), false).count()
        ).isEqualTo(0L);

        DBProcessInfo dbProcessInfo = new DBProcessInfo();
        dbProcessInfo.setProcessorType(ProcessorType.MP3_LOADER);
        dbProcessInfo.setProcessingStatus(ProcessingStatus.INFO);
        dbProcessInfo.setUpdateDateTime(LocalDateTime.of(2001, 9, 6, 2, 59, 59));

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
        dbProcessDetail.setUpdateDateTime(LocalDateTime.of(2023, 1, 12, 23, 2, 59));

        dbProcessDetailRepository.save(dbProcessDetail);

        List<DBProcessDetail> dbProcessDetails = StreamSupport
                .stream(dbProcessDetailRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        assertThat(dbProcessDetails.size()).isEqualTo(1L);

        DBProcessDetailAction dbProcessDetailAction = new DBProcessDetailAction();
        dbProcessDetailAction.setDbProcessDetail(dbProcessDetail);
        dbProcessDetailAction.setActionType(ProcessingActionType.ADD_ARTIST);
        dbProcessDetailAction.setValue("New Action");

        dbProcessDetailActionRepository.save(dbProcessDetailAction);

        List<DBProcessDetailAction> savedDBProcessDetailActions = dbProcessDetailActionRepository
                .findByDbProcessDetailOrderByIdAsc(dbProcessDetail);
        assertThat(savedDBProcessDetailActions.size()).isEqualTo(1);
        assertThat(savedDBProcessDetailActions.get(0).getValue()).isEqualTo("New Action");
        assertThat(savedDBProcessDetailActions.get(0).getActionType()).isEqualTo(ProcessingActionType.ADD_ARTIST);
        assertThat(savedDBProcessDetailActions.get(0).getDbProcessDetail().getId()).isEqualTo(dbProcessDetail.getId());
    }
}

