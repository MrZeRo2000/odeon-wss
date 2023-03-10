package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.ValueValidator;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import javax.sql.DataSource;
import java.util.List;
import java.util.logging.Logger;

import static com.romanpulov.odeonwss.db.DbManagerService.DbType.DB_IMPORTED_ARTISTS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIf(value = "${full.tests.disabled}", loadContext = true)
public class ServiceProcessLoadMusicDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadMusicDVTest.class.getSimpleName());

    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MUSIC_LOADER;
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

    private ProcessInfo executeProcessor() {
        processService.executeProcessor(PROCESSOR_TYPE);
        return processService.getProcessInfo();
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(value = false)
    void testPrepare() throws Exception {
        this.artifactType = artifactTypeRepository.getWithDVMusic();

        DbManagerService.loadOrPrepare(appConfiguration, DB_IMPORTED_ARTISTS, () -> {
            processService.executeProcessor(ProcessorType.ARTISTS_IMPORTER);
            assertThat(processService.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
            log.info("Artists Importer Processing info: " + processService.getProcessInfo());
        });
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void testContainsFilesShouldFail() {
        Assertions.assertEquals(0, artifactRepository.getAllByArtifactType(artifactType).size());

        processService.executeProcessor(PROCESSOR_TYPE, "");
        log.info("Music Loader Processing info: " + processService.getProcessInfo());

        assertThat(processService.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(artifactRepository.getAllByArtifactType(artifactType).size()).isEqualTo(0);
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void testSuccess() {
        ProcessInfo pi = executeProcessor();
        log.info("Music Loader Processing info: " + pi);
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Music Loader"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts loaded"),
                        ProcessingStatus.INFO,
                        3,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files loaded"),
                        ProcessingStatus.INFO,
                        4,
                        null)
        );

        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );
    }

    @Test
    @Order(4)
    @Rollback(value = false)
    void testSuccessRepeated() {
        int oldArtifacts = artifactRepository.getAllByArtifactType(artifactType).size();
        assertThat(oldArtifacts).isGreaterThan(0);

        int oldMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(artifactType).size();
        assertThat(oldMediaFiles).isGreaterThan(0);
        assertThat(oldMediaFiles).isGreaterThanOrEqualTo(oldArtifacts);

        ProcessInfo pi = executeProcessor();
        log.info("Music Loader Processing info: " + pi);
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Music Loader"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts loaded"),
                        ProcessingStatus.INFO,
                        0,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files loaded"),
                        ProcessingStatus.INFO,
                        0,
                        null)
        );

        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );

        int newArtifacts = artifactRepository.getAllByArtifactType(artifactType).size();
        assertThat(newArtifacts).isEqualTo(oldArtifacts);

        int newMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(artifactType).size();
        assertThat(newMediaFiles).isEqualTo(oldMediaFiles);
    }

    @Test
    @Order(5)
    @Rollback(value = false)
    void testSizeDuration() {
        artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .forEach(artifact -> {
                    assertThat(ValueValidator.isEmpty(artifact.getSize())).isFalse();
                    assertThat(ValueValidator.isEmpty(artifact.getDuration())).isFalse();
                });
    }
}
