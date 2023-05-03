package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.*;
import com.romanpulov.odeonwss.dto.DVProductDTO;
import com.romanpulov.odeonwss.dto.DVProductFlatDTO;
import com.romanpulov.odeonwss.dto.TextDTO;
import com.romanpulov.odeonwss.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryDVProductTests {

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private DVProductRepository dvProductRepository;

    @Autowired
    private DVOriginRepository dvOriginRepository;
    @Autowired
    private DVCategoryRepository dVCategoryRepository;
    @Autowired
    private TrackRepository trackRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
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
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
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
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
                .withOrigin(origins.values().stream().findFirst().orElseThrow())
                .withTitle("Product with categories")
                .withOriginalTitle("Original product with categories")
                .withYear(2003L)
                .withFrontInfo("Adriano Celentano")
                .withDescription("Best ever")
                .withNotes("Good as always")
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
                .withArtifactType(artifactTypeRepository.getWithDVAnimation())
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

        assertThat(savedProduct.getArtifactType().getId()).isEqualTo(artifactTypeRepository.getWithDVAnimation().getId());
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

    @Test
    @Order(5)
    void testFindByArtifactType() {
        var moviesList = dvProductRepository
                .findAllByArtifactTypeOrderByTitleAsc(artifactTypeRepository.getWithDVMovies());
        assertThat(moviesList.size()).isEqualTo(2);
        assertThat(moviesList.get(0).getTitle()).isEqualTo("Product with categories");
        assertThat(moviesList.get(0).getId()).isEqualTo(2L);
        assertThat(moviesList.get(1).getTitle()).isEqualTo("Title 1");
        assertThat(moviesList.get(1).getId()).isEqualTo(1L);

        var animationList = dvProductRepository
                .findAllByArtifactTypeOrderByTitleAsc(artifactTypeRepository.getWithDVAnimation());
        assertThat(animationList.size()).isEqualTo(1);
        assertThat(animationList.get(0).getId()).isEqualTo(3L);
        assertThat(animationList.get(0).getTitle()).isEqualTo("Product title");

        assertThat(dvProductRepository
                .findAllByArtifactTypeOrderByTitleAsc(artifactTypeRepository.getWithDVMusic()).size())
                .isEqualTo(0);
    }

    @Test
    @Order(6)
    void testFindDTOById() {
        DVProductDTO dto = dvProductRepository.findDTOById(1L).orElseThrow();
        assertThat(dto.getId()).isEqualTo(1L);
    }

    @Test
    @Order(7)
    @Transactional
    /*
    When annotated with transactional, it is possible to fetch lazy collections
    It generates additional queries
     */
    void testFindById() {
        DVProduct entity = dvProductRepository.findById(2L).orElseThrow();
        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getDvCategories().size()).isEqualTo(2);
    }

    @Test
    @Order(8)
    void testFindFlatDTOById() {
        List<DVProductFlatDTO> dtoList = dvProductRepository.findFlatDTOById(2L);
        assertThat(dtoList.size()).isEqualTo(2);

        DVProductFlatDTO dto1 = dtoList.get(0);
        DVProductFlatDTO dto2 = dtoList.get(1);

        assertThat(dto1.getArtifactTypeId()).isEqualTo(artifactTypeRepository.getWithDVMovies().getId());
        assertThat(dto1.getDvOriginId()).isEqualTo(1L);
        assertThat(dto1.getDvOriginName()).isEqualTo("Origin 1");
        assertThat(dto1.getTitle()).isEqualTo("Product with categories");
        assertThat(dto1.getOriginalTitle()).isEqualTo("Original product with categories");
        assertThat(dto1.getYear()).isEqualTo(2003L);
        assertThat(dto1.getFrontInfo()).isEqualTo("Adriano Celentano");
        assertThat(dto1.getDescription()).isEqualTo("Best ever");
        assertThat(dto1.getNotes()).isEqualTo("Good as always");
        assertThat(dto1.getDvCategoryId()).isEqualTo(2L);
        assertThat(dto1.getDvCategoryName()).isEqualTo("Cat 2");

        assertThat(dto2.getDvCategoryId()).isEqualTo(3L);
        assertThat(dto2.getDvCategoryName()).isEqualTo("Cat 3");
    }

    @Test
    @Order(8)
    void testFindDescriptionNotesById() {
        TextDTO description = dvProductRepository.findDescriptionById(2L).orElseThrow();
        assertThat(description.getText()).isEqualTo("Best ever");

        TextDTO notes = dvProductRepository.findNotesById(2L).orElseThrow();
        assertThat(notes.getText()).isEqualTo("Good as always");

        assertThatThrownBy(() -> dvProductRepository.findDescriptionById(22L).orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> dvProductRepository.findNotesById(77L).orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @Order(9)
    void testDeleteWithoutTracksShouldBeOk() {
        assertThat(dvProductRepository.findAll().size()).isEqualTo(3);
        assertThat(dvProductRepository.findDTOById(1L).orElseThrow()).isNotNull();

        dvProductRepository.deleteById(1L);

        assertThatThrownBy(() -> dvProductRepository.findDTOById(1L).orElseThrow()).isInstanceOf(NoSuchElementException.class);
        assertThat(dvProductRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @Order(10)
    void testDeleteWithTracksShouldFail() {
        Artifact artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
                .withTitle("Title 1")
                .build();
        artifactRepository.save(artifact);
        assertThat(artifact.getId()).isGreaterThan(0);

        DVProduct dvProduct = dvProductRepository.findById(2L).orElseThrow();

        Track track = new EntityTrackBuilder()
                .withArtifact(artifact)
                .withTitle("Track title")
                .withDiskNum(1L)
                .withNum(8L)
                .withDuration(123456L)
                .withMigrationId(4321L)
                .build();
        track.getDvProducts().add(dvProduct);

        trackRepository.save(track);
        assertThat(track.getId()).isGreaterThan(0);

        assertThatThrownBy(() -> dvProductRepository.delete(dvProduct)).isInstanceOf(JpaSystemException.class);
    }
}
