package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVCategoryBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVProductBuilder;
import com.romanpulov.odeonwss.entity.ArtifactType;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        Assertions.assertEquals(0, dvProductRepository.findAll().size());
        DVOrigin origin = dvOriginRepository.save(
                new EntityDVOriginBuilder()
                        .withId(1)
                        .withName("Origin 1")
                        .build()
        );
        DVProduct product = new EntityDVProductBuilder()
                .withArtifactType(ArtifactType.withDVMusic())
                .withOrigin(origin)
                .withTitle("Title 1")
                .build();
        dvProductRepository.save(product);

        assertThat(dvProductRepository.findAll().size()).isEqualTo(1);
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
                .withArtifactType(ArtifactType.withDVMusic())
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
                .withArtifactType(ArtifactType.withDVMovies())
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

        assertThat(savedProduct.getArtifactType().getId()).isEqualTo(ArtifactType.withDVMovies().getId());
        assertThat(savedProduct.getDvOrigin().getId()).isEqualTo(origin.getId());
        assertThat(savedProduct.getTitle()).isEqualTo("Product title");
        assertThat(savedProduct.getOriginalTitle()).isEqualTo("Product original title");
        assertThat(savedProduct.getYear()).isEqualTo(2000L);
        assertThat(savedProduct.getFrontInfo()).isEqualTo("Front info");
        assertThat(savedProduct.getDescription()).isEqualTo("Description");
        assertThat(savedProduct.getNotes()).isEqualTo("Notes");
        assertThat(savedProduct.getMigrationId()).isEqualTo(623);

        Map<Long, DVProduct> migrationIds = dvProductRepository.findAllMigrationIdMap();
        assertThat(migrationIds.size()).isEqualTo(1);
        assertThat(migrationIds.get(623L)).isNotNull();
    }

    @Test
    @Order(4)
    @Transactional
    @Rollback(value = false)
    void testAddRemoveCategory() {
        DVProduct product = dvProductRepository.findById(2L).orElseThrow();
        DVCategory newCategory = dVCategoryRepository.save(
                new EntityDVCategoryBuilder()
                        .withId(3)
                        .withName("Cat 3")
                        .build());

        Set<DVCategory> categories = product.getDvCategories();
        categories.removeIf(f -> f.getId() == 1L);
        categories.add(newCategory);
        product.setDvCategories(categories);
        dvProductRepository.save(product);

        product = dvProductRepository.findById(2L).orElseThrow();
        assertThat(product.getDvCategories().size()).isEqualTo(2);
        assertThat(product.getDvCategories().stream().filter(c -> c.getId() == 1L).count()).isEqualTo(0);
        assertThat(product.getDvCategories().stream().filter(c -> c.getId() == 3L).count()).isEqualTo(1);
    }
}
