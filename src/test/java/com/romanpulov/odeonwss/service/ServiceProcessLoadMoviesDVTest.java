package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.ValueValidator;
import com.romanpulov.odeonwss.service.processor.model.ProcessDetailInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import com.romanpulov.odeonwss.service.processor.model.ProcessDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import javax.sql.DataSource;
import java.util.logging.Logger;

import static com.romanpulov.odeonwss.db.DbManagerService.DbType.DB_PRODUCTS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIf(value = "${full.tests.disabled}", loadContext = true)
public class ServiceProcessLoadMoviesDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadMoviesDVTest.class.getSimpleName());

    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MOVIES_LOADER;
    private ArtifactType artifactType;

    @Autowired
    ProcessService processService;

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    DataSource dataSource;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(value = false)
    void testPrepare() throws Exception {
        this.artifactType = artifactTypeRepository.getWithDVMovies();

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
        Assertions.assertEquals(0, artifactRepository.getAllByArtifactType(artifactType).size());

        processService.executeProcessor(PROCESSOR_TYPE, "");
        log.info("Movies Loader Processing info: " + processService.getProcessInfo());

        Assertions.assertEquals(ProcessingStatus.FAILURE, processService.getProcessInfo().getProcessingStatus());
        Assertions.assertEquals(0, artifactRepository.getAllByArtifactType(artifactType).size());
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void testSuccess() {
        processService.executeProcessor(PROCESSOR_TYPE);
        log.info("Movies Loader Processing info: " + processService.getProcessInfo());

        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());
        Assertions.assertEquals(
                new ProcessDetail(ProcessDetailInfo.fromMessage("Artifacts loaded"), ProcessingStatus.INFO, 3, null),
                processService.getProcessInfo().getProcessDetails().get(1)
        );
        Assertions.assertEquals(
                new ProcessDetail(ProcessDetailInfo.fromMessage("Tracks loaded"), ProcessingStatus.INFO, 3, null),
                processService.getProcessInfo().getProcessDetails().get(2)
        );
        Assertions.assertEquals(
                new ProcessDetail(ProcessDetailInfo.fromMessage("Media files loaded"), ProcessingStatus.INFO, 4, null),
                processService.getProcessInfo().getProcessDetails().get(3)
        );

        trackRepository.getTracksByArtifactType(artifactType).forEach(c -> {
            Track productsTrack = trackRepository.findByIdWithProducts(c.getId()).orElseThrow();
            Assertions.assertEquals(1, productsTrack.getDvProducts().size());
        });
    }

    @Test
    @Order(4)
    @Rollback(value = false)
    void testSuccessRepeated() {
        int oldArtifacts = artifactRepository.getAllByArtifactType(artifactType).size();
        Assertions.assertTrue(oldArtifacts > 0);

        int oldTracks = trackRepository.getTracksByArtifactType(artifactType).size();
        Assertions.assertTrue(oldTracks > 0);
        Assertions.assertEquals(oldArtifacts, oldTracks);

        int oldMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(artifactType).size();
        Assertions.assertTrue(oldMediaFiles > 0);
        Assertions.assertTrue(oldMediaFiles >= oldTracks);

        processService.executeProcessor(PROCESSOR_TYPE);

        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());
        Assertions.assertEquals(
                new ProcessDetail(ProcessDetailInfo.fromMessage("Artifacts loaded"), ProcessingStatus.INFO, 0, null),
                processService.getProcessInfo().getProcessDetails().get(1)
        );
        Assertions.assertEquals(
                new ProcessDetail(ProcessDetailInfo.fromMessage("Tracks loaded"), ProcessingStatus.INFO, 0, null),
                processService.getProcessInfo().getProcessDetails().get(2)
        );
        Assertions.assertEquals(
                new ProcessDetail(ProcessDetailInfo.fromMessage("Media files loaded"), ProcessingStatus.INFO, 0, null),
                processService.getProcessInfo().getProcessDetails().get(3)
        );

        trackRepository.getTracksByArtifactType(artifactType).forEach(c -> {
            Track track = trackRepository.findByIdWithProducts(c.getId()).orElseThrow();
            Assertions.assertEquals(1, track.getDvProducts().size());
        });

        int newArtifacts = artifactRepository.getAllByArtifactType(artifactType).size();
        Assertions.assertEquals(oldArtifacts, newArtifacts);

        int newTracks = trackRepository.getTracksByArtifactType(artifactType).size();
        Assertions.assertEquals(oldTracks, newTracks);

        int newMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(artifactType).size();
        Assertions.assertEquals(oldMediaFiles, newMediaFiles);
    }

    @Test
    @Order(5)
    @Rollback(value = true)
    void testSizeDuration() {
        artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .forEach(artifact -> {
                    Assertions.assertFalse(ValueValidator.isEmpty(artifact.getSize()));
                    Assertions.assertFalse(ValueValidator.isEmpty(artifact.getDuration()));

                    Assertions.assertEquals(artifact.getDuration(), artifact.getTracks().get(0).getDuration());
                });
        trackRepository.getTracksByArtifactType(artifactType)
                .forEach(track -> {
                    Assertions.assertFalse(ValueValidator.isEmpty(track.getDuration()));
                });
    }
}
