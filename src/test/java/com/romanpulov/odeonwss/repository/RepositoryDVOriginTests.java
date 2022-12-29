package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.entity.DVOrigin;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryDVOriginTests {

    @Autowired
    private DVOriginRepository dvOriginRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql"})
    @Rollback(value = false)
    void testInsert() {
        Assertions.assertEquals(0, dvOriginRepository.getMaxId());

        DVOrigin origin = new EntityDVOriginBuilder()
                .withId(7)
                .withName("Origin 7")
                .build();
        dvOriginRepository.save(origin);

        Assertions.assertEquals(7, origin.getId());

        origin = new EntityDVOriginBuilder()
                .withId(5)
                .withName("Origin 5")
                .build();
        dvOriginRepository.save(origin);

        Assertions.assertEquals(5, origin.getId());

        Assertions.assertEquals(2, dvOriginRepository.findAllMap().size());
        Assertions.assertEquals(7, dvOriginRepository.getMaxId());
    }
}
