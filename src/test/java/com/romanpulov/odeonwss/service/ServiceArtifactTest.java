package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.ArtifactEditDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.builder.dtobuilder.ArtifactEditDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.ArtistCategoriesDetailDTOBuilder;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceArtifactTest {
    private static final Logger log = Logger.getLogger(ServiceArtifactTest.class.getSimpleName());

    @Autowired
    ArtifactService artifactService;

    @Autowired
    private ArtistService artistService;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testInsertShouldBeOk() throws Exception {
        ArtistCategoriesDetailDTO acd = artistService.insert(
                new ArtistCategoriesDetailDTOBuilder()
                        .withArtistName("Name 1")
                        .withArtistType(ArtistType.ARTIST)
                        .withArtistBiography("Bio 1")
                        .withGenre("Pop")
                        .withStyles("Electronic", "Rap", "Electronic")
                        .build()
        );

        ArtistCategoriesDetailDTO pacd = artistService.insert(
                new ArtistCategoriesDetailDTOBuilder()
                        .withArtistName("Performer Name 1")
                        .withArtistType(ArtistType.ARTIST)
                        .withArtistBiography("Performer Bio 1")
                        .withGenre("Pop")
                        .withStyles("Electronic", "Rap", "Electronic")
                        .build()
        );


        ArtifactEditDTO aed = new ArtifactEditDTOBuilder()
                .withArtifactTypeId(ArtifactType.withMP3().getId())
                .withArtistId(acd.getId())
                .withPerformerArtistId(pacd.getId())
                .withTitle("Title 1")
                .build();

        aed = artifactService.insert(aed);

        Assertions.assertEquals(1, aed.getId());
        Assertions.assertEquals(acd.getId(), aed.getArtistId());
        Assertions.assertEquals(pacd.getId(), aed.getPerformerArtistId());
    }

    @Test
    @Order(2)
    void testInsertWithoutArtistShouldFail() {
        Assertions.assertThrows(Exception.class, () -> {
            artifactService.insert(new ArtifactEditDTOBuilder()
                    .withArtifactTypeId(ArtifactType.withMP3().getId())
                    .withTitle("Title 2")
                    .build()
            );
        });
    }

    @Test
    @Order(3)
    void testInsertWithoutTitleShouldFail() {
        Assertions.assertThrows(Exception.class, () -> {
            artifactService.insert(new ArtifactEditDTOBuilder()
                    .withArtistId(1L)
                    .withArtifactTypeId(ArtifactType.withMP3().getId())
                    .build()
            );
        });
    }

    @Test
    @Order(4)
    void testUpdateYearShouldBeOk() throws Exception {
        ArtifactEditDTO aed = artifactService.getById(1L);
        aed.setYear(2000L);
        aed = artifactService.update(aed);

        Assertions.assertEquals(2000, aed.getYear());
    }

    @Test
    @Order(5)
    void testUpdateNotExistingShouldFail() {
        ArtifactEditDTO aed = new ArtifactEditDTOBuilder()
                .withId(888L)
                .build();
        Assertions.assertThrows(CommonEntityNotFoundException.class, () -> artifactService.update(aed));
    }
}
