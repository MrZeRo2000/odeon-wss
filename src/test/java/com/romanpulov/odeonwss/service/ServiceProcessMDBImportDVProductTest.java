package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.repository.DVCategoryRepository;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnabledIfEnvironmentVariable(named = "MDB_IMPORT", matches = "Y")
public class ServiceProcessMDBImportDVProductTest {

    @Autowired
    DVOriginRepository dvOriginRepository;

    @Autowired
    DVCategoryRepository dvCategoryRepository;

    @Autowired
    DVProductRepository dvProductRepository;

    @Autowired
    ProcessService service;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testImportDVProduct() {
        service.executeProcessor(ProcessorType.DV_PRODUCT_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        Assertions.assertEquals(4, dvOriginRepository.findAll().size());
        Assertions.assertEquals(4, dvOriginRepository.findAllMigrationIdMap().size());
        Assertions.assertTrue(dvOriginRepository.findAllMigrationIdMap().keySet().containsAll(List.of(1L, 2L, 17L, 76L)));

        Assertions.assertFalse(dvCategoryRepository.findAll().isEmpty());
        Assertions.assertFalse(dvProductRepository.findAll().isEmpty());
    }

    @Test
    @Order(2)
    void testImportDVProductRepeated() {
        int oldOrigins = dvOriginRepository.findAll().size();
        int oldCategories = dvCategoryRepository.findAll().size();
        int oldProducts = dvProductRepository.findAll().size();

        service.executeProcessor(ProcessorType.DV_PRODUCT_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        Assertions.assertEquals(oldOrigins, dvOriginRepository.findAll().size());
        Assertions.assertEquals(oldCategories, dvCategoryRepository.findAll().size());
        Assertions.assertEquals(oldProducts, dvProductRepository.findAll().size());
    }
}
