package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVCategoryBuilder;
import com.romanpulov.odeonwss.entity.DVCategory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryDVCategoryTests {

    @Autowired
    private DVCategoryRepository dvCategoryRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql"})
    @Rollback(value = false)
    void testInsert() {
        Assertions.assertEquals(0, dvCategoryRepository.getMaxId());

        DVCategory category = new EntityDVCategoryBuilder()
                .withName("Category 33")
                .withMigrationId(312)
                .build();
        dvCategoryRepository.save(category);

        Assertions.assertEquals(1, category.getId());
        Assertions.assertEquals(312, category.getMigrationId());

        category = new EntityDVCategoryBuilder()
                .withName("Category 66")
                .build();
        dvCategoryRepository.save(category);

        Assertions.assertEquals(2, category.getId());

        Assertions.assertEquals(2, dvCategoryRepository.findAllMap().size());
        Assertions.assertEquals(2, dvCategoryRepository.getMaxId());

        Map<Long, DVCategory> migrationIds = dvCategoryRepository.findAllMigrationIdMap();
        Assertions.assertEquals(1, migrationIds.size());
        Assertions.assertNotNull(migrationIds.get(312L));
    }

    @Test
    @Order(2)
    void testDuplicateName() {
        Assertions.assertThrows(JpaSystemException.class, () ->
            dvCategoryRepository.save(
                    new EntityDVCategoryBuilder()
                            .withName("Category 66")
                            .build()
            )
        );

        Assertions.assertThrows(JpaSystemException.class, () ->
            dvCategoryRepository.save(
                    new EntityDVCategoryBuilder()
                            .withName("Category 66")
                            .build()
            )
        );
    }

    @Test
    @Order(3)
    void testFindAllOrderByName() {
        var categories = dvCategoryRepository.findAllDTO();

        assertThat(categories.size()).isEqualTo(2);
        assertThat(categories.get(0).getId()).isEqualTo(1);
        assertThat(categories.get(0).getName()).isEqualTo("Category 33");
        assertThat(categories.get(1).getId()).isEqualTo(2);
        assertThat(categories.get(1).getName()).isEqualTo("Category 66");
    }

    @Test
    @Order(4)
    void testFindDVOriginById() {
        assertThat(dvCategoryRepository.findDTOById(2L).orElseThrow().getName()).isEqualTo("Category 66");
        Assertions.assertThrows(NoSuchElementException.class, () -> dvCategoryRepository.findDTOById(40L).orElseThrow());
    }

}
