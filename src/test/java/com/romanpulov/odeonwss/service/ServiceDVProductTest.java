package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.dtobuilder.DVCategoryDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.DVOriginDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.DVProductDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityTrackBuilder;
import com.romanpulov.odeonwss.dto.DVCategoryDTO;
import com.romanpulov.odeonwss.dto.DVOriginDTO;
import com.romanpulov.odeonwss.dto.DVProductDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceDVProductTest {
    private static final Logger log = Logger.getLogger(ServiceDVProductTest.class.getSimpleName());

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private DVOriginService dvOriginService;

    @Autowired
    private DVCategoryService dvCategoryService;

    @Autowired
    private DVProductService service;

    @Autowired
    private DVProductRepository dvProductRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testPrepareDataShouldBeOk() throws Exception {
        DVOriginDTO dvOriginDTO1 = new DVOriginDTOBuilder()
                .withName("Initial origin")
                .build();
        dvOriginDTO1 = dvOriginService.insert(dvOriginDTO1);
        log.info("Inserted dvOriginDTO1:" + dvOriginDTO1);

        assertThat(dvOriginDTO1.getId()).isEqualTo(1L);
        assertThat(dvOriginDTO1.getName()).isEqualTo("Initial origin");

        DVOriginDTO dvOriginDTO2 = new DVOriginDTOBuilder()
                .withName("Another origin")
                .build();
        dvOriginDTO2 = dvOriginService.insert(dvOriginDTO2);
        log.info("Inserted dvOriginDTO2:" + dvOriginDTO2);

        assertThat(dvOriginDTO2.getId()).isEqualTo(2L);
        assertThat(dvOriginDTO2.getName()).isEqualTo("Another origin");

        DVCategoryDTO dvCategoryDTO1 = new DVCategoryDTOBuilder()
                .withName("First category")
                .build();

        DVCategoryDTO dvCategoryDTO2 = new DVCategoryDTOBuilder()
                .withName("Another category")
                .build();

        DVCategoryDTO savedDvCategoryDTO1 = dvCategoryService.insert(dvCategoryDTO1);
        assertThat(savedDvCategoryDTO1.getId()).isEqualTo(1L);
        assertThat(savedDvCategoryDTO1.getName()).isEqualTo(dvCategoryDTO1.getName());

        DVCategoryDTO savedDvCategoryDTO2 = dvCategoryService.insert(dvCategoryDTO2);
        assertThat(savedDvCategoryDTO2.getId()).isEqualTo(2L);
        assertThat(savedDvCategoryDTO2.getName()).isEqualTo(dvCategoryDTO2.getName());
    }

    @Test
    @Order(2)
    void testInsertNewWithoutCategoriesShouldBeOk() throws Exception {
        DVOriginDTO dvOriginDTO = dvOriginService.getById(1L);

        DVProductDTO dvProductDTO = new DVProductDTOBuilder()
                .withArtifactTypeId(artifactTypeRepository.getWithDVMovies().getId())
                .withDvOrigin(dvOriginDTO)
                .withTitle("First title")
                .withOriginalTitle("First original title")
                .withYear(2010L)
                .withFrontInfo("Front first")
                .withDescription("First description")
                .withNotes("First notes")
                .build();
        dvProductDTO = service.insert(dvProductDTO);
        log.info("Inserted dvProductDTO:" + dvProductDTO);

        assertThat(dvProductDTO.getId()).isEqualTo(1L);
        assertThat(dvProductDTO.getArtifactTypeId()).isEqualTo(artifactTypeRepository.getWithDVMovies().getId());
        assertThat(dvProductDTO.getDvOrigin().getId()).isEqualTo(1L);
        assertThat(dvProductDTO.getDvOrigin().getName()).isEqualTo("Initial origin");
        assertThat(dvProductDTO.getTitle()).isEqualTo("First title");
        assertThat(dvProductDTO.getOriginalTitle()).isEqualTo("First original title");
        assertThat(dvProductDTO.getYear()).isEqualTo(2010L);
        assertThat(dvProductDTO.getFrontInfo()).isEqualTo("Front first");
        assertThat(dvProductDTO.getDescription()).isEqualTo("First description");
        assertThat(dvProductDTO.getNotes()).isEqualTo("First notes");
        assertThat(dvProductDTO.getDvCategories().size()).isEqualTo(0);
    }

    @Test
    @Order(3)
    void testInsertNewWithoutTitleShouldFail() throws Exception {
        DVOriginDTO dvOriginDTO = dvOriginService.getById(1L);

        DVProductDTO dvProductDTO = new DVProductDTOBuilder()
                .withArtifactTypeId(artifactTypeRepository.getWithDVMovies().getId())
                .withDvOrigin(dvOriginDTO)
                .withOriginalTitle("Failed original title")
                .withYear(2010L)
                .withFrontInfo("Failed first")
                .withDescription("Failed description")
                .withNotes("Failed notes")
                .build();

        assertThatThrownBy(() -> service.insert(dvProductDTO)).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @Order(4)
    void testInsertWithCategoriesShouldBeOk() throws Exception {
        DVOriginDTO dvOriginDTO = dvOriginService.getById(1L);
        DVCategoryDTO dvCategoryDTO1 = dvCategoryService.getById(1L);
        DVCategoryDTO dvCategoryDTO2 = dvCategoryService.getById(2L);

        DVProductDTO dvProductDTO = new DVProductDTOBuilder()
                .withArtifactTypeId(artifactTypeRepository.getWithDVMovies().getId())
                .withDvOrigin(dvOriginDTO)
                .withTitle("With categories")
                .withOriginalTitle("With categories original title")
                .withYear(2011L)
                .withDvCategories(List.of(dvCategoryDTO1, dvCategoryDTO2))
                .build();

        DVProductDTO savedDvProductDTO = service.insert(dvProductDTO);
        log.info("Inserted dvProductDTO:" + savedDvProductDTO);

        assertThat(savedDvProductDTO.getDvCategories().size()).isEqualTo(2);
        assertThat(savedDvProductDTO.getDvCategories().get(0).getId()).isEqualTo(2L);
        assertThat(savedDvProductDTO.getDvCategories().get(0).getName()).isEqualTo("Another category");
        assertThat(savedDvProductDTO.getDvCategories().get(1).getId()).isEqualTo(1L);
        assertThat(savedDvProductDTO.getDvCategories().get(1).getName()).isEqualTo("First category");
    }

    @Test
    @Order(5)
    void testUpdateAddCategoriesShouldBeOk() throws Exception {
        DVProductDTO dvProductDTO = service.getById(1L);
        assertThat(dvProductDTO.getDvCategories().size()).isEqualTo(0);

        DVCategoryDTO dvCategoryDTO = dvCategoryService.getById(2L);
        dvProductDTO.getDvCategories().add(dvCategoryDTO);

        DVProductDTO savedDvProductDTO = service.update(dvProductDTO);
        log.info("Updated dvProductDTO:" + savedDvProductDTO);

        assertThat(savedDvProductDTO.getDvCategories().size()).isEqualTo(1);
        assertThat(savedDvProductDTO.getDvCategories().get(0).getId()).isEqualTo(dvCategoryDTO.getId());
        assertThat(savedDvProductDTO.getDvCategories().get(0).getName()).isEqualTo(dvCategoryDTO.getName());
    }

    @Test
    @Order(6)
    void testUpdateChangeCategoriesShouldBeOk() throws Exception {
        DVProductDTO dvProductDTO = service.getById(1L);
        assertThat(dvProductDTO.getDvCategories().size()).isEqualTo(1);
        assertThat(dvProductDTO.getDvCategories().get(0).getName()).isEqualTo("Another category");

        DVCategoryDTO dvCategoryDTO = dvCategoryService.getById(1L);
        dvProductDTO.getDvCategories().clear();
        dvProductDTO.getDvCategories().add(dvCategoryDTO);

        DVProductDTO savedDvProductDTO = service.update(dvProductDTO);
        log.info("Updated dvProductDTO:" + savedDvProductDTO);

        assertThat(savedDvProductDTO.getDvCategories().size()).isEqualTo(1);
        assertThat(savedDvProductDTO.getDvCategories().get(0).getName()).isEqualTo("First category");
    }

    @Test
    @Order(7)
    void testUpdateRemoveCategoriesShouldBeOk() throws Exception {
        DVProductDTO dvProductDTO = service.getById(1L);
        assertThat(dvProductDTO.getDvCategories().size()).isEqualTo(1);
        dvProductDTO.getDvCategories().clear();

        DVProductDTO savedDvProductDTO = service.update(dvProductDTO);
        log.info("Updated dvProductDTO:" + savedDvProductDTO);

        assertThat(savedDvProductDTO.getDvCategories().size()).isEqualTo(0);
    }

    @Test
    @Order(8)
    void testGetTableDifferentOriginsShouldBeOk() throws Exception {
        DVOriginDTO dvOriginDTO = dvOriginService.getById(2L);

        DVProductDTO dvProductDTO = new DVProductDTOBuilder()
                .withArtifactTypeId(artifactTypeRepository.getWithDVMovies().getId())
                .withDvOrigin(dvOriginDTO)
                .withTitle("Another title")
                .withOriginalTitle("Another original title")
                .withYear(2014L)
                .withFrontInfo("Front another")
                .withDescription("Another description")
                .withNotes("Another notes")
                .build();
        dvProductDTO = service.insert(dvProductDTO);
        log.info("Inserted dvProductDTO:" + dvProductDTO);

        Artifact artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
                .withTitle("Title 1")
                .build();
        artifactRepository.save(artifact);
        assertThat(artifact.getId()).isGreaterThan(0);

        Track track = new EntityTrackBuilder()
                .withArtifact(artifact)
                .withTitle("Track title")
                .withDiskNum(1L)
                .withNum(8L)
                .withDuration(123456L)
                .withMigrationId(4321L)
                .build();
        track.getDvProducts().add(dvProductRepository.findById(dvProductDTO.getId()).orElseThrow());

        trackRepository.save(track);
        assertThat(track.getId()).isGreaterThan(0);

        var table = service.getTable(artifactTypeRepository.getWithDVMovies().getId());
        assertThat(table.size()).isEqualTo(3);
        assertThat(table.get(0).getDvOrigin().getId()).isNull();
        assertThat(table.get(0).getDvOrigin().getName()).isEqualTo("Another origin");
        assertThat(table.get(0).getTitle()).isEqualTo("Another title");
        assertThat(table.get(0).getOriginalTitle()).isEqualTo("Another original title");
        assertThat(table.get(0).getYear()).isEqualTo(2014L);
        assertThat(table.get(0).getFrontInfo()).isEqualTo("Front another");
        assertThat(table.get(0).getDescription()).isNull();
        assertThat(table.get(0).getHasDescription()).isTrue();
        assertThat(table.get(0).getNotes()).isNull();
        assertThat(table.get(0).getHasNotes()).isTrue();
        assertThat(table.get(0).getHasTracks()).isTrue();

        assertThat(table.get(1).getDvOrigin().getName()).isEqualTo("Initial origin");
        assertThat(table.get(2).getDvOrigin().getName()).isEqualTo("Initial origin");

        assertThat(table.get(1).getHasTracks()).isNull();
        assertThat(table.get(2).getHasTracks()).isNull();

        assertThat(table.get(0).getDvCategories().size()).isEqualTo(0);
        assertThat(table.get(1).getDvCategories().size()).isEqualTo(0);
        assertThat(table.get(2).getDvCategories().size()).isEqualTo(2);
        assertThat(table.get(2).getDvCategories().get(0).getId()).isNull();
        assertThat(table.get(2).getDvCategories().get(0).getName()).isEqualTo("Another category");
        assertThat(table.get(2).getDvCategories().get(1).getName()).isEqualTo("First category");
    }
}
