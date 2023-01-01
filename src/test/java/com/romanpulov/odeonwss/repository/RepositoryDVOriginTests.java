package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.entity.DVOrigin;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import javax.validation.ConstraintViolationException;

import java.util.Map;

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
                .withName("Origin 7")
                .build();
        dvOriginRepository.save(origin);

        Assertions.assertEquals(1, origin.getId());

        origin = new EntityDVOriginBuilder()
                .withName("Origin 5")
                .withMigrationId(13)
                .build();
        dvOriginRepository.save(origin);

        Assertions.assertEquals(2, origin.getId());

        Map<Long, DVOrigin> dvOrigins = dvOriginRepository.findAllMap();
        Assertions.assertEquals(2, dvOrigins.size());
        Assertions.assertEquals(13, dvOrigins.get(2L).getMigrationId());
        Assertions.assertEquals(2, dvOriginRepository.getMaxId());
    }

    @Test
    @Order(2)
    void testDuplicateName() {
        Assertions.assertThrows(JpaSystemException.class, () -> {
            dvOriginRepository.save(
                    new EntityDVOriginBuilder()
                            .withId(55)
                            .withName("Origin 5")
                            .build()
            );
        });
    }

    @Test
    @Order(2)
    void testNoName() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            dvOriginRepository.save(
                    new EntityDVOriginBuilder()
                            .build()
            );
        });
    }
}
