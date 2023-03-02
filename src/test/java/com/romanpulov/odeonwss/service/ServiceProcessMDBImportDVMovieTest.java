package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIf(value = "${full.tests.disabled}", loadContext = true)
public class ServiceProcessMDBImportDVMovieTest {

    public static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MOVIES_IMPORTER;
    @Autowired
    ProcessService service;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    CompositionRepository compositionRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    AppConfiguration appConfiguration;

    private ProcessInfo executeProcessor() {
        service.executeProcessor(PROCESSOR_TYPE);
        return service.getProcessInfo();
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testImportDVMovie() {
        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        assertThat(processDetails.get(0)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Movies importer"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(1).getInfo().getMessage()).isEqualTo("Categories imported");
        assertThat(processDetails.get(1).getRows()).isGreaterThan(0);

        assertThat(processDetails.get(2).getInfo().getMessage()).isEqualTo("Products imported");
        assertThat(processDetails.get(2).getRows()).isGreaterThan(0);
        assertThat(processDetails.get(2).getRows()).isGreaterThan(processDetails.get(1).getRows());

        assertThat(processDetails.get(3).getInfo().getMessage()).isEqualTo("Artifacts imported");
        assertThat(processDetails.get(3).getRows()).isGreaterThan(0);

        assertThat(processDetails.get(4).getInfo().getMessage()).isEqualTo("Compositions imported");
        assertThat(processDetails.get(4).getRows()).isGreaterThan(0);
        assertThat(processDetails.get(4).getRows()).isEqualTo(processDetails.get(3).getRows());

        assertThat(processDetails.get(5).getInfo().getMessage()).isEqualTo("Products for compositions imported");
        assertThat(processDetails.get(5).getRows()).isGreaterThan(0);

        assertThat(processDetails.get(6).getInfo().getMessage()).isEqualTo("Media files imported");
        assertThat(processDetails.get(6).getRows()).isGreaterThan(0);
        assertThat(processDetails.get(6).getRows()).isGreaterThanOrEqualTo(processDetails.get(4).getRows());

        assertThat(processDetails.get(7)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );
    }

    @Test
    @Order(2)
    void testImportDVMovieRepeated() {
        int oldArtifacts = artifactRepository.getAllByArtifactType(ArtifactType.withDVMovies()).size();
        int oldCompositions = compositionRepository.getCompositionsByArtifactType(ArtifactType.withDVMovies()).size();
        int oldMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(ArtifactType.withDVMovies()).size();

        assertThat(oldArtifacts).isGreaterThan(0);
        assertThat(oldCompositions).isGreaterThan(0);
        assertThat(oldMediaFiles).isGreaterThan(0);
        assertThat(oldCompositions).isEqualTo(oldArtifacts);
        assertThat(oldMediaFiles).isGreaterThanOrEqualTo(oldCompositions);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        assertThat(processDetails.get(1).getRows()).isEqualTo(0);
        assertThat(processDetails.get(2).getRows()).isEqualTo(0);
        assertThat(processDetails.get(3).getRows()).isEqualTo(0);
        assertThat(processDetails.get(4).getRows()).isEqualTo(0);
        assertThat(processDetails.get(5).getRows()).isEqualTo(0);
        assertThat(processDetails.get(6).getRows()).isEqualTo(0);

        int newArtifacts = artifactRepository.getAllByArtifactType(ArtifactType.withDVMovies()).size();
        int newCompositions = compositionRepository.getCompositionsByArtifactType(ArtifactType.withDVMovies()).size();
        int newMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(ArtifactType.withDVMovies()).size();

        assertThat(newArtifacts).isEqualTo(oldArtifacts);
        assertThat(newCompositions).isEqualTo(oldCompositions);
        assertThat(newMediaFiles).isEqualTo(oldMediaFiles);
        assertThat(newCompositions).isEqualTo(oldCompositions);
    }

    @Test
    @Order(3)
    void testProductsForAll() {
        compositionRepository.getCompositionsByArtifactType(ArtifactType.withDVMovies()).forEach(
                c -> {
                    Composition composition = compositionRepository.findByIdWithProducts(c.getId()).orElseThrow();
                    assertThat(composition.getDvProducts().size()).isEqualTo(1);
                }
        );
    }
}
