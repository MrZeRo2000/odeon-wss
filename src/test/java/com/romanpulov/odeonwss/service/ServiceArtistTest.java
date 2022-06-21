package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.builder.dtobuilder.ArtistCategoriesDetailDTOBuilder;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceArtistTest {

    private static final Logger log = Logger.getLogger(ServiceArtistTest.class.getSimpleName());

    @Autowired
    private ArtistService artistService;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void createNewShouldBeOk() throws Exception {
        ArtistCategoriesDetailDTO acd = artistService.insert(
            new ArtistCategoriesDetailDTOBuilder()
                    .withArtistName("Name 1")
                    .withArtistType(ArtistType.ARTIST)
                    .withArtistBiography("Bio 1")
                    .withGenre("Pop")
                    .withStyles("Electronic", "Rap", "Electronic")
                    .build()
        );

        Assertions.assertEquals(1L, acd.getId());
        Assertions.assertEquals("Name 1", acd.getArtistName());
        Assertions.assertEquals("Bio 1", acd.getArtistBiography());
        Assertions.assertEquals(ArtistType.ARTIST, acd.getArtistType());
        Assertions.assertEquals("Pop", acd.getGenre());
        Assertions.assertEquals(List.of("Electronic", "Rap"), acd.getStyles());
    }

    @Test
    @Order(2)
    void updateNotExistingShouldFail() throws Exception {
        ArtistCategoriesDetailDTO acd = artistService.getById(1L);
        Assertions.assertNotNull(acd);
        Assertions.assertNotNull(acd.getId());

        acd.setId(12L);
        Assertions.assertThrows(CommonEntityNotFoundException.class, () -> artistService.update(acd));
    }

    @Test
    @Order(3)
    void updateStylesWithDuplicates() throws Exception {
        ArtistCategoriesDetailDTO acd = artistService.getById(1L);
        acd.setStyles(List.of("Rap", "Dance", "R&B", "Dance"));
        acd = artistService.update(acd);
        Assertions.assertEquals(List.of("Dance", "R&B", "Rap"), acd.getStyles());
    }

    @Test
    @Order(4)
    void updateStylesReduced() throws Exception {
        ArtistCategoriesDetailDTO acd = artistService.getById(1L);
        acd.setStyles(List.of("Rap"));
        acd = artistService.update(acd);
        Assertions.assertEquals(List.of("Rap"), acd.getStyles());
    }

    @Test
    @Order(5)
    void deleteNotExistingShouldFail() throws Exception {
        Assertions.assertThrows(CommonEntityNotFoundException.class, () -> artistService.deleteById(777L));
    }

    @Test
    @Order(6)
    void insertWithConflictShouldFail() throws Exception {
        Assertions.assertThrows(CommonEntityAlreadyExistsException.class, () ->
                artistService.insert(
                        new ArtistCategoriesDetailDTOBuilder()
                                .withArtistName("Name 1")
                                .withArtistType(ArtistType.ARTIST)
                                .withArtistBiography("Bio 3")
                                .withGenre("Rock")
                                .build()
                )
                );
    }

    @Test
    @Order(7)
    void updateWithConflictShouldFail() throws Exception {
        artistService.insert(
                new ArtistCategoriesDetailDTOBuilder()
                        .withArtistName("Name 2")
                        .withArtistType(ArtistType.ARTIST)
                        .withArtistBiography("Bio 2")
                        .withGenre("Rock")
                        .build()
        );

        ArtistCategoriesDetailDTO acd = artistService.getById(1L);
        acd.setArtistName("Name 2");
        Assertions.assertThrows(CommonEntityAlreadyExistsException.class, () ->
                artistService.update(acd));

        acd.setArtistName("Name 3");
        artistService.update(acd);
    }

    @Test
    @Order(8)
    void deleteExistingShouldBeOk() throws Exception {
        artistService.deleteById(2L);
    }

    @Test
    @Order(9)
    void saveWithoutBiographyShouldBeOk() throws Exception {
        ArtistCategoriesDetailDTO acd = artistService.insert(
                new ArtistCategoriesDetailDTOBuilder()
                        .withArtistName("Name 9")
                        .withArtistType(ArtistType.ARTIST)
                        .withGenre("Pop")
                        .build()
        );

        acd.setArtistName("Name 99");
        acd = artistService.update(acd);

        Assertions.assertNull(acd.getArtistBiography());

        acd.setArtistBiography("Bio 99");
        acd = artistService.update(acd);
        Assertions.assertEquals("Bio 99", acd.getArtistBiography());

        acd.setArtistBiography("Bio 999");
        acd = artistService.update(acd);
        Assertions.assertEquals("Bio 999", acd.getArtistBiography());

        acd.setArtistBiography(null);
        acd = artistService.update(acd);
        Assertions.assertNull(acd.getArtistBiography());
    }

    @Test
    @Order(10)
    void saveWithNameOnlyShouldBeOk() throws Exception {
        ArtistCategoriesDetailDTO acd = artistService.insert(
                new ArtistCategoriesDetailDTOBuilder()
                        .withArtistName("Name 10")
                        .build()
        );

        acd.setArtistName("Name 10 1");
        acd = artistService.update(acd);
        Assertions.assertEquals("Name 10 1", acd.getArtistName());
    }

}
