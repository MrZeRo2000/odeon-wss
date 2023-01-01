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

import javax.transaction.Transactional;
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

    @Test
    @Order(3)
    @Rollback(value = false)
    void testInsertFields() {
        DVOrigin origin = dvOriginRepository.findAllMap().values().stream().findFirst().orElseThrow();
        DVProduct product = new EntityDVProductBuilder()
                .withOrigin(origin)
                .withTitle("Product title")
                .withOriginalTitle("Product original title")
                .withYear(2000L)
                .withFrontInfo("Front info")
                .withDescription("Description")
                .withNotes("Notes")
                .withMigrationId(623)
                .build();
        dvProductRepository.save(product);

        DVProduct savedProduct = dvProductRepository.findById(3L).orElseThrow();

        Assertions.assertEquals(origin.getId(), savedProduct.getDvOrigin().getId());
        Assertions.assertEquals("Product title", savedProduct.getTitle());
        Assertions.assertEquals("Product original title", savedProduct.getOriginalTitle());
        Assertions.assertEquals(2000L, savedProduct.getYear());
        Assertions.assertEquals("Front info", savedProduct.getFrontInfo());
        Assertions.assertEquals("Description", savedProduct.getDescription());
        Assertions.assertEquals("Notes", savedProduct.getNotes());
        Assertions.assertEquals(623, savedProduct.getMigrationId());
    }

    @Test
    @Order(4)
    @Transactional
    @Rollback(value = false)
    void testAddRemoveCategory() {
        DVProduct product = dvProductRepository.findById(2L).orElseThrow();
        DVCategory newCategory = dVCategoryRepository.save(new EntityDVCategoryBuilder().withId(3).withName("Cat 3").build());

        Set<DVCategory> categories = product.getDvCategories();
        categories.removeIf(f -> f.getId() == 1L);
        categories.add(newCategory);
        product.setDvCategories(categories);
        dvProductRepository.save(product);

        product = dvProductRepository.findById(2L).orElseThrow();
        Assertions.assertEquals(2, product.getDvCategories().size());
        Assertions.assertEquals(0, product.getDvCategories().stream().filter(c -> c.getId() == 1L).count());
        Assertions.assertEquals(1, product.getDvCategories().stream().filter(c -> c.getId() == 3L).count());
    }
}
