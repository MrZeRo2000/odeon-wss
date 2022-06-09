package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.dtobuilder.ArtistCategoriesDetailDTOBuilder;
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
        ArtistCategoriesDetailDTO acd = artistService.insertACD(
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
        ArtistCategoriesDetailDTO acd = artistService.getACDById(1L);
        Assertions.assertNotNull(acd);
        Assertions.assertNotNull(acd.getId());

        acd.setId(12L);
        Assertions.assertThrows(CommonEntityNotFoundException.class, () -> artistService.updateACD(acd));
    }

    @Test
    @Order(3)
    void updateStylesWithDuplicates() throws Exception {
        ArtistCategoriesDetailDTO acd = artistService.getACDById(1L);
        acd.setStyles(List.of("Rap", "Dance", "R&B", "Dance"));
        acd = artistService.updateACD(acd);
        Assertions.assertEquals(List.of("Dance", "R&B", "Rap"), acd.getStyles());
    }

    @Test
    @Order(4)
    void updateStylesReduced() throws Exception {
        ArtistCategoriesDetailDTO acd = artistService.getACDById(1L);
        acd.setStyles(List.of("Rap"));
        acd = artistService.updateACD(acd);
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
                artistService.insertACD(
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
        artistService.insertACD(
                new ArtistCategoriesDetailDTOBuilder()
                        .withArtistName("Name 2")
                        .withArtistType(ArtistType.ARTIST)
                        .withArtistBiography("Bio 2")
                        .withGenre("Rock")
                        .build()
        );

        ArtistCategoriesDetailDTO acd = artistService.getACDById(1L);
        acd.setArtistName("Name 2");
        Assertions.assertThrows(CommonEntityAlreadyExistsException.class, () ->
                artistService.updateACD(acd));

        acd.setArtistName("Name 3");
        artistService.updateACD(acd);
    }

    @Test
    @Order(8)
    void deleteExistingShouldBeOk() throws Exception {
        artistService.deleteById(2L);
    }

    @Test
    @Order(9)
    void saveWithoutBiographyShouldBeOk() throws Exception {
        ArtistCategoriesDetailDTO acd = artistService.insertACD(
                new ArtistCategoriesDetailDTOBuilder()
                        .withArtistName("Name 9")
                        .withArtistType(ArtistType.ARTIST)
                        .withGenre("Pop")
                        .build()
        );

        acd.setArtistName("Name 99");
        acd = artistService.updateACD(acd);

        Assertions.assertNull(acd.getArtistBiography());

        acd.setArtistBiography("Bio 99");
        acd = artistService.updateACD(acd);
        Assertions.assertEquals("Bio 99", acd.getArtistBiography());

        acd.setArtistBiography("Bio 999");
        acd = artistService.updateACD(acd);
        Assertions.assertEquals("Bio 999", acd.getArtistBiography());

        acd.setArtistBiography(null);
        acd = artistService.updateACD(acd);
        Assertions.assertNull(acd.getArtistBiography());
    }

}
