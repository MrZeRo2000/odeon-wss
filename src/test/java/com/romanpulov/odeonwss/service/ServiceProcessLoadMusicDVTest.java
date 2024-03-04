package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.ValueValidator;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(value = "test-02")
public class ServiceProcessLoadMusicDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadMusicDVTest.class.getSimpleName());

    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MUSIC_LOADER;
    private ArtifactType artifactType;

    @Autowired
    DataSource dataSource;

    @Autowired
    ProcessService processService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    private ProcessInfo executeProcessor() {
        processService.executeProcessor(PROCESSOR_TYPE);
        return processService.getProcessInfo();
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(value = false)
    void testPrepare() {
        this.artifactType = artifactTypeRepository.getWithDVMusic();

        Arrays.asList("The Cure", "Tori Amos", "Various Artists").forEach(s ->
                artistRepository.save(
                        new EntityArtistBuilder()
                                .withType(ArtistType.ARTIST)
                                .withName(s)
                                .build()
                ));

        log.info("Created artists");
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
                        ProcessDetailInfo.fromMessage("Started Video music Loader"),
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

        var artifacts = artifactRepository.getAllByArtifactType(this.artifactType);
        assertThat(artifacts.get(0).getTitle()).isEqualTo("Beautiful Voices 1");
        assertThat(artifacts.get(0).getArtist()).isNull();

        assertThat(artifacts.get(1).getTitle()).isEqualTo("The Cure - Picture Show 1991");
        assertThat(artifacts.get(1).getArtist()).isNotNull();
        assertThat(Optional.ofNullable(artifacts.get(1).getArtist()).orElseThrow().getId())
                .isEqualTo(artistRepository.findFirstByName("The Cure").orElseThrow().getId());

        assertThat(artifacts.get(2).getTitle()).isEqualTo("Tori Amos - Fade to Red 2006");
        assertThat(artifacts.get(2).getArtist()).isNotNull();
        assertThat(Optional.ofNullable(artifacts.get(2).getArtist()).orElseThrow().getId())
                .isEqualTo(artistRepository.findFirstByName("Tori Amos").orElseThrow().getId());
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
                        ProcessDetailInfo.fromMessage("Started Video music Loader"),
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
