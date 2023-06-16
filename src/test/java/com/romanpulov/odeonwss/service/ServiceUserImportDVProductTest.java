package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVProductBuilder;
import com.romanpulov.odeonwss.dto.IdTitleDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
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
}
