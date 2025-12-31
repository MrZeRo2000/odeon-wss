package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.dtobuilder.DVCategoryDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.DVProductUserImportDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.DVProductUserImportDetailDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVCategoryBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVProductBuilder;
import com.romanpulov.odeonwss.dto.DVCategoryDTO;
import com.romanpulov.odeonwss.dto.IdTitleOriginalTitleDTO;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceUserImportDVProductTest {
    final List<String> PRODUCT_NAMES = List.of(
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

    @Autowired
    DVProductService dvProductService;

    private ArtifactType getArtifactType() {
        return artifactTypeRepository.getWithDVMovies();
    }

    private void internalPrepare() {
        DVOrigin dvOrigin = dvOriginRepository.save((new EntityDVOriginBuilder()).withName("Greece").build());
        PRODUCT_NAMES.forEach(s -> dvProductRepository.save(
                (new EntityDVProductBuilder())
                        .withOrigin(dvOrigin)
                        .withArtifactType(getArtifactType())
                        .withTitle(s)
                        .withOriginalTitle(s + "(original)")
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
        List<IdTitleOriginalTitleDTO> dvProducts = dvProductRepository.findAllIdTitleOriginalTitle(getArtifactType());
        assertThat(dvProducts.size()).isEqualTo(2);
        assertThat(dvProducts.get(0).getTitle()).isEqualTo("Cruel Summer");
        assertThat(dvProducts.get(0).getOriginalTitle()).isEqualTo("Cruel Summer(original)");
    }

    @Test
    @Order(2)
    void testIncompleteParametersShouldFail() {
        final var dataNoArtifactType = (new DVProductUserImportDTOBuilder()).build();
        assertThatThrownBy(() -> service.analyzeImportDVProducts(dataNoArtifactType)).isInstanceOf(EmptyParameterException.class);

        var dataNoOrigin = (new DVProductUserImportDTOBuilder())
                .withArtifactTypeId(getArtifactType().getId())
                .build();
        assertThatThrownBy(() -> service.analyzeImportDVProducts(dataNoOrigin)).isInstanceOf(EmptyParameterException.class);

        var dataWrongCategories = (new DVProductUserImportDTOBuilder())
                .withArtifactTypeId(getArtifactType().getId())
                .withDvOriginId(dvOriginRepository.findById(1L).orElseThrow().getId())
                .withDvCategories(List.of(
                        (new DVCategoryDTOBuilder()).withName("Cat 01").build(),
                        (new DVCategoryDTOBuilder()).withName("Unknown Cat").build()
                        ))
                .build();
        assertThatThrownBy(() -> service.analyzeImportDVProducts(dataWrongCategories)).isInstanceOf(CommonEntityNotFoundException.class);
    }

    @Test
    @Order(3)
    void testOneNewShouldBeOk() throws Exception {
        var data = (new DVProductUserImportDTOBuilder())
                .withArtifactTypeId(getArtifactType().getId())
                .withDvOriginId(dvOriginRepository.findById(1L).orElseThrow().getId())
                .withFrontInfo("Olivia Holt")
                .withDvCategories(
                        List.of(
                                (new DVCategoryDTOBuilder()).withName("Cat 01").build()
                        )
                )
                .withDvProductDetails(List.of(
                        (new DVProductUserImportDetailDTOBuilder()
                                .withTitle("New Title")
                                .withOriginalTitle("New Original Title")
                                .withYear(1999L)
                                .build()),
                        (new DVProductUserImportDetailDTOBuilder()
                                .withTitle("Cruel Summer")
                                .withOriginalTitle("Cruel Summer")
                                .withYear(2003L)
                                .build())
                ))
                .build();

        var ar = service.analyzeImportDVProducts(data);
        assertThat(ar.getRowsInserted().size()).isEqualTo(1);
        assertThat(ar.getRowsInserted().get(0)).isEqualTo("New Title");

        assertThat(ar.getRowsUpdated().size()).isEqualTo(1);
        assertThat(ar.getRowsUpdated().get(0)).isEqualTo("Cruel Summer");

        var er = service.executeImportDVProducts(data);
        assertThat(er.getRowsInserted().size()).isEqualTo(1);
        assertThat(er.getRowsInserted().get(0)).isEqualTo("New Title");

        assertThat(er.getRowsUpdated().size()).isEqualTo(1);
        assertThat(er.getRowsUpdated().get(0)).isEqualTo("Cruel Summer");

        var products = dvProductService.getTable(getArtifactType().getId());
        assertThat(products.size()).isEqualTo(3);
        // "The Idol", "Cruel Summer", "New Title"

        assertThat(products.get(0).getTitle()).isEqualTo("Cruel Summer");
        assertThat(products.get(0).getFrontInfo()).isEqualTo("Olivia Holt");
        assertThat(products.get(0).getYear()).isEqualTo(2003L);
        assertThat(
                products.get(0)
                        .getDvCategories()
                        .stream()
                        .map(DVCategoryDTO::getName)
                        .collect(Collectors.toList())).isEqualTo(List.of("Cat 01"));

        assertThat(products.get(1).getTitle()).isEqualTo("New Title");
        assertThat(products.get(1).getOriginalTitle()).isEqualTo("New Original Title");
        assertThat(products.get(1).getFrontInfo()).isEqualTo("Olivia Holt");
        assertThat(products.get(1).getYear()).isEqualTo(1999L);
        assertThat(products.get(1).getDvCategories().size()).isEqualTo(1);

        assertThat(products.get(2).getTitle()).isEqualTo("The Idol");
        assertThat(products.get(2).getDvCategories().size()).isEqualTo(0);
        assertThat(products.get(2).getFrontInfo()).isNull();
    }
}
