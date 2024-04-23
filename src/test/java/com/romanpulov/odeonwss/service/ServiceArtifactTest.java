package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.dtobuilder.ArtifactDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.ArtistDTOBuilder;
import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.ArtifactDTOImpl;
import com.romanpulov.odeonwss.dto.ArtistDTO;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceArtifactTest {
    private static final Logger log = Logger.getLogger(ServiceArtifactTest.class.getSimpleName());

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtifactService artifactService;

    @Autowired
    private ArtistService artistService;
    @Autowired
    private ArtistRepository artistRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testInsertShouldBeOk() throws Exception {
        ArtistDTO acd = artistService.insert(
                new ArtistDTOBuilder()
                        .withArtistName("Name 1")
                        .withArtistType(ArtistType.ARTIST)
                        .withArtistBiography("Bio 1")
                        .withGenre("Pop")
                        .withStyles(List.of("Electronic", "Rap", "Electronic"))
                        .build()
        );

        ArtistDTO pacd = artistService.insert(
                new ArtistDTOBuilder()
                        .withArtistName("Performer Name 1")
                        .withArtistType(ArtistType.ARTIST)
                        .withArtistBiography("Performer Bio 1")
                        .withGenre("Pop")
                        .withStyles(List.of("Electronic", "Rap", "Electronic"))
                        .build()
        );


        ArtifactDTO aed = new ArtifactDTOBuilder()
                .withArtifactTypeId(artifactTypeRepository.getWithMP3().getId())
                .withArtist(acd)
                .withPerformerArtist(pacd)
                .withTitle("Title 1")
                .build();

        ArtifactDTO insertedDTO = artifactService.insert(aed);

        Assertions.assertEquals(1, insertedDTO.getId());
        Assertions.assertEquals(acd.getId(), insertedDTO.getArtist().getId());
        Assertions.assertEquals(pacd.getId(), insertedDTO.getPerformerArtist().getId());
    }

    @Test
    @Order(2)
    void testInsertWithoutArtistShouldBeOk() throws Exception {
        artifactService.insert(new ArtifactDTOBuilder()
                .withArtifactTypeId(artifactTypeRepository.getWithDVMovies().getId())
                .withTitle("Title 2")
                .build()
        );
    }

    @Test
    @Order(3)
    void testInsertWithoutTitleShouldFail() {
        Assertions.assertThrows(Exception.class, () -> artifactService.insert(new ArtifactDTOBuilder()
                .withArtist(artistRepository.findDTOById(1L).orElseThrow())
                .withArtifactTypeId(artifactTypeRepository.getWithMP3().getId())
                .build()
        ));
    }

    @Test
    @Order(4)
    void testUpdateYearShouldBeOk() throws Exception {
        ArtifactDTO aed = artifactService.getById(1L);

        ArtifactDTOImpl dto = ArtifactDTOImpl.fromArtifactDTO(aed);

        dto.setYear(2000L);
        var updatedDTO = artifactService.update(dto);

        Assertions.assertEquals(2000, updatedDTO.getYear());
    }

    @Test
    @Order(5)
    void testUpdateNotExistingShouldFail() {
        ArtifactDTO aed = new ArtifactDTOBuilder()
                .withId(888L)
                .build();
        Assertions.assertThrows(CommonEntityNotFoundException.class, () -> artifactService.update(aed));
    }

    @Test
    @Order(5)
    void testArtifactWithTags() throws Exception {
        long artifactTypeId = artifactTypeRepository.getWithDVMovies().getId();
        var artifact = artifactService.insert(
            new ArtifactDTOBuilder()
                    .withArtifactTypeId(artifactTypeId)
                    .withTitle("Artifact with tags")
                    .withTags(List.of("Yellow", "Green"))
                    .build()
        );

        var dto = artifactService.getTable(ArtistType.ARTIST, List.of(artifactTypeId))
                .stream()
                .filter(v -> v.getId().equals(artifact.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(dto.getTags().size()).isEqualTo(2);
        assertThat(dto.getTags().get(0)).isEqualTo("Green");
        assertThat(dto.getTags().get(1)).isEqualTo("Yellow");
    }
}
