package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIf(value = "${full.tests.disabled}", loadContext = true)
public class ServiceProcessMDBImportDVMusicTest {
    private static final Logger log = Logger.getLogger(ServiceProcessMDBImportClassicsTest.class.getSimpleName());
    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MUSIC_IMPORTER;

    private ArtifactType artifactType;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    ProcessService service;

    @Autowired
    AppConfiguration appConfiguration;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testLoadArtists() {
        this.artifactType = artifactTypeRepository.getWithDVMusic();

        DbManagerService.loadOrPrepare(appConfiguration, DbManagerService.DbType.DB_IMPORTED_ARTISTS, () -> {
            service.executeProcessor(ProcessorType.ARTISTS_IMPORTER);
            Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
            log.info("Artist Importer Processing info: " + service.getProcessInfo());
        });

        log.info("Processing info: " + service.getProcessInfo());
    }

    private ProcessInfo executeProcessor() {
        service.executeProcessor(PROCESSOR_TYPE);
        return service.getProcessInfo();
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testLoadDVMusic() {
        ProcessInfo pi = executeProcessor();
        log.info("Processing info: " + service.getProcessInfo());
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Video music importer"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts imported"),
                        ProcessingStatus.INFO,
                        124,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Tracks imported"),
                        ProcessingStatus.INFO,
                        2208,
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
    @Order(3)
    @Rollback(false)
    void testLoadDVMusicTwo() {
        int oldArtifactsCount = artifactRepository.getAllByArtifactType(artifactType).size();
        assertThat(oldArtifactsCount).isGreaterThan(0);

        long oldTracksCount = StreamSupport.stream(trackRepository.findAll().spliterator(), false).count();
        assertThat(oldTracksCount).isGreaterThan(0);

        long oldMediaFilesCount = StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count();
        assertThat(oldMediaFilesCount).isGreaterThan(0);

        ProcessInfo pi = executeProcessor();
        log.info("Processing info: " + service.getProcessInfo());
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        assertThat(processDetails.get(1).getRows()).isEqualTo(0);
        assertThat(processDetails.get(2).getRows()).isEqualTo(0);

        int newArtifactsCount = artifactRepository.getAllByArtifactType(artifactType).size();
        assertThat(newArtifactsCount).isGreaterThan(0);
        assertThat(newArtifactsCount).isEqualTo(oldArtifactsCount);

        long newTracksCount = StreamSupport.stream(trackRepository.findAll().spliterator(), false).count();
        assertThat(newTracksCount).isGreaterThan(0);
        assertThat(newTracksCount).isEqualTo(oldTracksCount);

        long newMediaFilesCount = StreamSupport.stream(mediaFileRepository.findAll().spliterator(), false).count();
        assertThat(newMediaFilesCount).isGreaterThan(0);
        assertThat(newMediaFilesCount).isEqualTo(oldMediaFilesCount);
    }
}
