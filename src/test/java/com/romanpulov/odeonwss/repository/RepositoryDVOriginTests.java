package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import javax.validation.ConstraintViolationException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        assertThat(dvOriginRepository.getMaxId()).isEqualTo(0);

        DVOrigin origin = new EntityDVOriginBuilder()
                .withName("Origin 7")
                .build();
        dvOriginRepository.save(origin);

        assertThat(origin.getId()).isEqualTo(1);

        origin = new EntityDVOriginBuilder()
                .withName("Origin 5")
                .withMigrationId(13)
                .build();
        dvOriginRepository.save(origin);

        assertThat(origin.getId()).isEqualTo(2);

        Map<Long, DVOrigin> dvOrigins = dvOriginRepository.findAllMap();
        assertThat(dvOrigins.size()).isEqualTo(2);
        assertThat(dvOrigins.get(2L).getMigrationId()).isEqualTo(13);
        assertThat(dvOriginRepository.getMaxId()).isEqualTo(2);

        Map<Long, DVOrigin> migrationIds = dvOriginRepository.findAllMigrationIdMap();
        assertThat(migrationIds.size()).isEqualTo(1);
        assertThat(migrationIds.get(13L)).isNotNull();
        assertThat(migrationIds.get(12L)).isNull();
    }

    @Test
    @Order(2)
    void testDuplicateName() {
        Assertions.assertThrows(JpaSystemException.class, () ->
            dvOriginRepository.save(
                    new EntityDVOriginBuilder()
                            .withId(55)
                            .withName("Origin 5")
                            .build()
            )
        );
    }

    @Test
    @Order(2)
    void testNoName() {
        Assertions.assertThrows(ConstraintViolationException.class, () ->
            dvOriginRepository.save(
                    new EntityDVOriginBuilder()
                            .build()
            )
        );
    }

    @Test
    @Order(3)
    void testFindAllOrderByName() {
        List<IdNameDTO> origins = dvOriginRepository.findAllByOrderByName();
        assertThat(origins.size()).isEqualTo(2);
        assertThat(origins.get(0).getName()).isEqualTo("Origin 5");
        assertThat(origins.get(0).getId()).isEqualTo(2);
        assertThat(origins.get(1).getName()).isEqualTo("Origin 7");
        assertThat(origins.get(1).getId()).isEqualTo(1);
    }

    @Test
    @Order(4)
    void testFindDVOriginById() {
        assertThat(dvOriginRepository.findDVOriginById(2).orElseThrow().getName()).isEqualTo("Origin 5");
        Assertions.assertThrows(NoSuchElementException.class, () -> dvOriginRepository.findDVOriginById(40L).orElseThrow());
    }
}
