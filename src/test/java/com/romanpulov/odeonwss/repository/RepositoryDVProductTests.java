package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVCategoryBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVProductBuilder;
import com.romanpulov.odeonwss.entity.DVCategory;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.entity.DVProduct;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryDVProductTests {

    @Autowired
    private DVProductRepository dvProductRepository;

    @Autowired
    private DVOriginRepository dvOriginRepository;
    @Autowired
    private DVCategoryRepository dVCategoryRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql"})
    @Rollback(value = false)
    void testMinimumInsert() {
        Assertions.assertEquals(0, StreamSupport.stream(dvProductRepository.findAll().spliterator(), false).count());
        DVOrigin origin = dvOriginRepository.save(
                new EntityDVOriginBuilder()
                        .withId(1)
                        .withName("Origin 1")
                        .build()
        );
        DVProduct product = new EntityDVProductBuilder()
                .withOrigin(origin)
                .withTitle("Title 1")
                .build();
        dvProductRepository.save(product);

        Assertions.assertEquals(1, StreamSupport.stream(dvProductRepository.findAll().spliterator(), false).count());
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void testInsertWithCategories() {
        //origin
        Map<Long, DVOrigin> origins = dvOriginRepository.findAllMap();

        //categories
        dVCategoryRepository.save(new EntityDVCategoryBuilder().withId(1).withName("Cat 1").build());
        dVCategoryRepository.save(new EntityDVCategoryBuilder().withId(2).withName("Cat 2").build());
        Set<DVCategory> categories = new HashSet<>(dVCategoryRepository.findAllMap().values());

        DVProduct product = new EntityDVProductBuilder()
                .withOrigin(origins.values().stream().findFirst().orElseThrow())
                .withTitle("Product with categories")
                .withCategories(categories)
                .build();
        dvProductRepository.save(product);
    }
}
