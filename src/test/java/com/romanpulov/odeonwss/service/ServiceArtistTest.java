package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.dtobuilder.ArtistDTOBuilder;
import com.romanpulov.odeonwss.dto.ArtistDTO;
import com.romanpulov.odeonwss.dto.ArtistDTOImpl;
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
        ArtistDTO acd = artistService.insert(
            new ArtistDTOBuilder()
                    .withArtistName("Name 1")
                    .withArtistType(ArtistType.ARTIST)
                    .withArtistBiography("Bio 1")
                    .withGenre("Pop")
                    .withStyles(List.of("Electronic", "Rap", "Electronic"))
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
        ArtistDTO acd = artistService.getById(1L);
        Assertions.assertNotNull(acd);
        Assertions.assertNotNull(acd.getId());

        acd.setId(12L);
        Assertions.assertThrows(CommonEntityNotFoundException.class, () -> artistService.update(acd));
    }

    @Test
    @Order(3)
    void updateStylesWithDuplicates() throws Exception {
        ArtistDTO acd = artistService.getById(1L);
        ArtistDTOImpl dto = ArtistDTOImpl.fromArtistDTO(acd);

        dto.setStyles(List.of("Rap", "Dance", "R&B", "Dance"));
        ArtistDTO updatedDTO = artistService.update(dto);
        Assertions.assertEquals(List.of("Dance", "R&B", "Rap"), updatedDTO.getStyles());
    }

    @Test
    @Order(4)
    void updateStylesReduced() throws Exception {
        ArtistDTO acd = artistService.getById(1L);
        ArtistDTOImpl dto = ArtistDTOImpl.fromArtistDTO(acd);
        dto.setStyles(List.of("Rap"));
        ArtistDTO updatedDTO = artistService.update(dto);
        Assertions.assertEquals(List.of("Rap"), updatedDTO.getStyles());
    }

    @Test
    @Order(5)
    void deleteNotExistingShouldFail() {
        Assertions.assertThrows(CommonEntityNotFoundException.class, () -> artistService.deleteById(777L));
    }

    @Test
    @Order(6)
    void insertWithConflictShouldFail() {
        Assertions.assertThrows(CommonEntityAlreadyExistsException.class, () ->
                artistService.insert(
                        new ArtistDTOBuilder()
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
                new ArtistDTOBuilder()
                        .withArtistName("Name 2")
                        .withArtistType(ArtistType.ARTIST)
                        .withArtistBiography("Bio 2")
                        .withGenre("Rock")
                        .build()
        );

        ArtistDTO acd = artistService.getById(1L);
        ArtistDTOImpl dto = ArtistDTOImpl.fromArtistDTO(acd);
        dto.setArtistName("Name 2");
        Assertions.assertThrows(CommonEntityAlreadyExistsException.class, () ->
                artistService.update(dto));

        dto.setArtistName("Name 3");
        artistService.update(dto);
    }

    @Test
    @Order(8)
    void deleteExistingShouldBeOk() throws Exception {
        artistService.deleteById(2L);
    }

    @Test
    @Order(9)
    void saveWithoutBiographyShouldBeOk() throws Exception {
        ArtistDTO acd = artistService.insert(
                new ArtistDTOBuilder()
                        .withArtistName("Name 9")
                        .withArtistType(ArtistType.ARTIST)
                        .withGenre("Pop")
                        .build()
        );
        ArtistDTOImpl dto = ArtistDTOImpl.fromArtistDTO(acd);

        dto.setArtistName("Name 99");
        ArtistDTO updatedDTO = artistService.update(dto);

        Assertions.assertNull(updatedDTO.getArtistBiography());

        dto.setArtistBiography("Bio 99");
        updatedDTO = artistService.update(dto);
        Assertions.assertEquals("Bio 99", updatedDTO.getArtistBiography());

        dto.setArtistBiography("Bio 999");
        updatedDTO = artistService.update(dto);
        Assertions.assertEquals("Bio 999", updatedDTO.getArtistBiography());

        dto.setArtistBiography(null);
        updatedDTO = artistService.update(dto);
        Assertions.assertNull(updatedDTO.getArtistBiography());
    }

    @Test
    @Order(10)
    void saveWithNameOnlyShouldBeOk() throws Exception {
        ArtistDTO acd = artistService.insert(
                new ArtistDTOBuilder()
                        .withArtistName("Name 10")
                        .build()
        );
        ArtistDTOImpl dto = ArtistDTOImpl.fromArtistDTO(acd);

        dto.setArtistName("Name 10 1");
        ArtistDTO updatedDTO = artistService.update(dto);
        Assertions.assertEquals("Name 10 1", updatedDTO.getArtistName());
    }
}
