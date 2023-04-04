package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.dtobuilder.DVCategoryDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.DVOriginDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.DVProductDTOBuilder;
import com.romanpulov.odeonwss.dto.DVCategoryDTO;
import com.romanpulov.odeonwss.dto.DVOriginDTO;
import com.romanpulov.odeonwss.dto.DVProductDTO;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
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

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testPrepareDataShouldBeOk() throws Exception {
        DVOriginDTO dvOriginDTO = new DVOriginDTOBuilder()
                .withName("Initial origin")
                .build();
        dvOriginDTO = dvOriginService.insert(dvOriginDTO);
        log.info("Inserted dvOriginDTO:" + dvOriginDTO);

        assertThat(dvOriginDTO.getId()).isEqualTo(1L);
        assertThat(dvOriginDTO.getName()).isEqualTo("Initial origin");

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
}
