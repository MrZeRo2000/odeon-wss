package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVCategoryBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.entity.DVCategory;
import com.romanpulov.odeonwss.entity.DVOrigin;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

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
                .withId(7)
                .withName("Category 33")
                .build();
        dvCategoryRepository.save(category);

        Assertions.assertEquals(7, category.getId());

        category = new EntityDVCategoryBuilder()
                .withId(66)
                .withName("Category 66")
                .build();
        dvCategoryRepository.save(category);

        Assertions.assertEquals(66, category.getId());

        Assertions.assertEquals(2, dvCategoryRepository.findAllMap().size());
        Assertions.assertEquals(66, dvCategoryRepository.getMaxId());
    }

    @Test
    @Order(2)
    void testDuplicateName() {
        Assertions.assertThrows(JpaSystemException.class, () -> {
            dvCategoryRepository.save(
                    new EntityDVCategoryBuilder()
                            .withId(55)
                            .withName("Category 66")
                            .build()
            );
        });
    }
}
