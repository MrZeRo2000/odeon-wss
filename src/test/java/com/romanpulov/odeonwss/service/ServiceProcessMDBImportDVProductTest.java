package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.repository.DVCategoryRepository;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessMDBImportDVProductTest {

    @Autowired
    DVOriginRepository dvOriginRepository;

    @Autowired
    DVCategoryRepository dvCategoryRepository;

    @Autowired
    ProcessService service;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    //@Rollback(false)
    void testImportDVProduct() {
        service.executeProcessor(ProcessorType.DV_PRODUCT_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        Assertions.assertEquals(4, dvOriginRepository.findAll().size());
        Assertions.assertEquals(4, dvOriginRepository.findAllMigrationIdMap().size());
        Assertions.assertTrue(dvOriginRepository.findAllMigrationIdMap().keySet().containsAll(List.of(1L, 2L, 17L, 76L)));
    }

    @Test
    @Order(2)
    void testImportDVProductRepeated() {
        int oldOrigins = dvOriginRepository.findAll().size();
        int oldCategories = dvCategoryRepository.findAll().size();

        service.executeProcessor(ProcessorType.DV_PRODUCT_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        Assertions.assertEquals(oldOrigins, dvOriginRepository.findAll().size());
        Assertions.assertEquals(oldCategories, dvCategoryRepository.findAll().size());
    }

}
