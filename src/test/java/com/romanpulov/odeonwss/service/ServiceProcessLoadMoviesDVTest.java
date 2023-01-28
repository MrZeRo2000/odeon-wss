package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.ValueValidator;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import com.romanpulov.odeonwss.service.processor.model.ProgressDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.romanpulov.odeonwss.db.DbManagerService.DbType.DB_PRODUCTS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIf(value = "${full.tests.disabled}", loadContext = true)
public class ServiceProcessLoadMoviesDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadMoviesDVTest.class.getSimpleName());

    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MOVIES_LOADER;
    private static final ArtifactType ARTIFACT_TYPE = ArtifactType.withDVMovies();

    @Autowired
    ProcessService processService;

    @Autowired
    private AppConfiguration appConfiguration;
    @Autowired
    private ArtifactRepository artifactRepository;
    @Autowired
    private CompositionRepository compositionRepository;
    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(value = false)
    void testPrepare() {
        DbManagerService.loadOrPrepare(appConfiguration, DB_PRODUCTS, () -> {
            processService.executeProcessor(ProcessorType.DV_PRODUCT_IMPORTER);
            Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());
            log.info("Products Importer Processing info: " + processService.getProcessInfo());
        });
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void testContainsFilesShouldFail() {
        Assertions.assertEquals(0, artifactRepository.getAllByArtifactType(ARTIFACT_TYPE).size());

        processService.executeProcessor(PROCESSOR_TYPE, "");
        log.info("Movies Loader Processing info: " + processService.getProcessInfo());

        Assertions.assertEquals(ProcessingStatus.FAILURE, processService.getProcessInfo().getProcessingStatus());
        Assertions.assertEquals(0, artifactRepository.getAllByArtifactType(ARTIFACT_TYPE).size());
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void testSuccess() {
        processService.executeProcessor(PROCESSOR_TYPE);
        log.info("Movies Loader Processing info: " + processService.getProcessInfo());

        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());
        Assertions.assertEquals(
                new ProgressDetail(ProgressDetail.ProgressInfo.fromMessage("Artifacts loaded"), ProcessingStatus.INFO, 3, null),
                processService.getProcessInfo().getProgressDetails().get(1)
        );
        Assertions.assertEquals(
                new ProgressDetail(ProgressDetail.ProgressInfo.fromMessage("Compositions loaded"), ProcessingStatus.INFO, 3, null),
                processService.getProcessInfo().getProgressDetails().get(2)
        );
        Assertions.assertEquals(
                new ProgressDetail(ProgressDetail.ProgressInfo.fromMessage("Media files loaded"), ProcessingStatus.INFO, 4, null),
                processService.getProcessInfo().getProgressDetails().get(3)
        );

        compositionRepository.getCompositionsByArtifactType(ARTIFACT_TYPE).forEach(c -> {
            Composition productsComposition = compositionRepository.findByIdWithProducts(c.getId()).orElseThrow();
            Assertions.assertEquals(1, productsComposition.getDvProducts().size());
        });
    }

    @Test
    @Order(4)
    @Rollback(value = false)
    void testSuccessRepeated() {
        int oldArtifacts = artifactRepository.getAllByArtifactType(ARTIFACT_TYPE).size();
        Assertions.assertTrue(oldArtifacts > 0);

        int oldCompositions = compositionRepository.getCompositionsByArtifactType(ARTIFACT_TYPE).size();
        Assertions.assertTrue(oldCompositions > 0);
        Assertions.assertEquals(oldArtifacts, oldCompositions);

        int oldMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(ARTIFACT_TYPE).size();
        Assertions.assertTrue(oldMediaFiles > 0);
        Assertions.assertTrue(oldMediaFiles >= oldCompositions);

        processService.executeProcessor(PROCESSOR_TYPE);

        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());
        Assertions.assertEquals(
                new ProgressDetail(ProgressDetail.ProgressInfo.fromMessage("Artifacts loaded"), ProcessingStatus.INFO, 0, null),
                processService.getProcessInfo().getProgressDetails().get(1)
        );
        Assertions.assertEquals(
                new ProgressDetail(ProgressDetail.ProgressInfo.fromMessage("Compositions loaded"), ProcessingStatus.INFO, 0, null),
                processService.getProcessInfo().getProgressDetails().get(2)
        );
        Assertions.assertEquals(
                new ProgressDetail(ProgressDetail.ProgressInfo.fromMessage("Media files loaded"), ProcessingStatus.INFO, 0, null),
                processService.getProcessInfo().getProgressDetails().get(3)
        );

        compositionRepository.getCompositionsByArtifactType(ARTIFACT_TYPE).forEach(c -> {
            Composition composition = compositionRepository.findByIdWithProducts(c.getId()).orElseThrow();
            Assertions.assertEquals(1, composition.getDvProducts().size());
        });

        int newArtifacts = artifactRepository.getAllByArtifactType(ARTIFACT_TYPE).size();
        Assertions.assertEquals(oldArtifacts, newArtifacts);

        int newCompositions = compositionRepository.getCompositionsByArtifactType(ARTIFACT_TYPE).size();
        Assertions.assertEquals(oldCompositions, newCompositions);

        int newMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(ARTIFACT_TYPE).size();
        Assertions.assertEquals(oldMediaFiles, newMediaFiles);
    }

    @Test
    @Order(5)
    @Rollback(value = true)
    void testSizeDuration() {
        artifactRepository.getAllByArtifactTypeWithCompositions(ARTIFACT_TYPE)
                .forEach(artifact -> {
                    Assertions.assertFalse(ValueValidator.isEmpty(artifact.getSize()));
                    Assertions.assertFalse(ValueValidator.isEmpty(artifact.getDuration()));

                    Assertions.assertEquals(artifact.getDuration(), artifact.getCompositions().get(0).getDuration());
                });
        compositionRepository.getCompositionsByArtifactType(ARTIFACT_TYPE)
                .forEach(composition -> {
                    Assertions.assertFalse(ValueValidator.isEmpty(composition.getDuration()));
                });
    }
}
