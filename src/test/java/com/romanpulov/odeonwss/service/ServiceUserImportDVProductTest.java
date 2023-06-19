package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.dtobuilder.DVCategoryDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.DVProductUserImportDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVCategoryBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVProductBuilder;
import com.romanpulov.odeonwss.dto.DVProductUserImportDTO;
import com.romanpulov.odeonwss.dto.IdTitleDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.EmptyParameterException;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.DVCategoryRepository;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import com.romanpulov.odeonwss.service.user.DVProductUserImportService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceUserImportDVProductTest {
    List<String> PRODUCT_NAMES = List.of(
            "The Idol",
            "Cruel Summer"
    );

    @Autowired
    DVProductUserImportService service;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    DVOriginRepository dvOriginRepository;

    @Autowired
    DVCategoryRepository dvCategoryRepository;

    @Autowired
    DVProductRepository dvProductRepository;

    private void internalPrepare() {
        DVOrigin dvOrigin = dvOriginRepository.save((new EntityDVOriginBuilder()).withName("Greece").build());
        ArtifactType artifactType = artifactTypeRepository.getWithDVMovies();
        PRODUCT_NAMES.forEach(s -> dvProductRepository.save(
                (new EntityDVProductBuilder())
                        .withOrigin(dvOrigin)
                        .withArtifactType(artifactType)
                        .withTitle(s)
                        .build()
        ));
        dvCategoryRepository.saveAll(List.of(
                (new EntityDVCategoryBuilder()).withName("Cat 01").build(),
                (new EntityDVCategoryBuilder()).withName("Cat 02").build()
        ));
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testPrepareShouldBeOk() {
        internalPrepare();
        ArtifactType artifactType = artifactTypeRepository.getWithDVMovies();
        List<IdTitleDTO> dvProducts = dvProductRepository.findAllByArtifactTypeOrderByTitleAsc(artifactType);
        assertThat(dvProducts.size()).isEqualTo(2);
        assertThat(dvProducts.get(0).getTitle()).isEqualTo("Cruel Summer");
    }

    @Test
    @Order(2)
    void testIncompleteParametersShouldFail() {
        final var dataNoArtifactType = (new DVProductUserImportDTOBuilder()).build();
        assertThatThrownBy(() -> service.analyzeImportDVProducts(dataNoArtifactType)).isInstanceOf(EmptyParameterException.class);

        var dataNoOrigin = (new DVProductUserImportDTOBuilder())
                .withArtifactTypeId(artifactTypeRepository.getWithDVMovies().getId())
                .build();
        assertThatThrownBy(() -> service.analyzeImportDVProducts(dataNoOrigin)).isInstanceOf(EmptyParameterException.class);

        var dataWrongCategories = (new DVProductUserImportDTOBuilder())
                .withArtifactTypeId(artifactTypeRepository.getWithDVMovies().getId())
                .withDvOriginId(dvOriginRepository.findById(1L).orElseThrow().getId())
                .withDvCategories(List.of(
                        (new DVCategoryDTOBuilder()).withName("Cat 01").build(),
                        (new DVCategoryDTOBuilder()).withName("Unknown Cat").build()
                        ))
                .build();
        assertThatThrownBy(() -> service.analyzeImportDVProducts(dataWrongCategories)).isInstanceOf(CommonEntityNotFoundException.class);

    }
}
