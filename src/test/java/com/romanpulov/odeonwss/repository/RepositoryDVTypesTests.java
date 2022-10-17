package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryDVTypesTests {

    @Autowired
    DVTypeRepository dvTypeRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testData() {
        List<DVType> dvTypeList = dvTypeRepository.getAllByOrderById();

        Assertions.assertEquals(8, dvTypeList.size());
        Assertions.assertEquals("DVD Rip", dvTypeList.get(7).getName());
        Assertions.assertEquals("AVC", dvTypeList.get(6).getName());
    }

    @Test
    @Order(2)
    void testGetMap() {
        Map<Long, DVType> dvTypeMap = dvTypeRepository.findAllMap();

        Assertions.assertEquals(8, dvTypeMap.size());
        Assertions.assertEquals("MPEG4 DVD", dvTypeMap.get(2L).getName());
        Assertions.assertEquals("DVD", dvTypeMap.get(3L).getName());
    }
}
