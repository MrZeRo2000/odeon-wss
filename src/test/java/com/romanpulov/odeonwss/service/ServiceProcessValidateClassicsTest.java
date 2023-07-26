package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateClassicsTest {

    private static final Logger log = Logger.getLogger(ServiceProcessValidateClassicsTest.class.getName());

    @Autowired
    ProcessService service;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    TrackRepository trackRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testLoad() {
        service.executeProcessor(ProcessorType.CLASSICS_IMPORTER);

        log.info("Processing info: " + service.getProcessInfo());
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(2)
    void testValidateFailed() {
        service.executeProcessor(ProcessorType.CLASSICS_VALIDATOR);
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        log.info("Processing info: " + service.getProcessInfo());
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Classics validator"),
                        ProcessingStatus.INFO,
                        null,
                        null));

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts validated"),
                        ProcessingStatus.INFO,
                        null,
                        null));

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Track numbers for artifact not increasing monotonically",
                                List.of("Шопен Фредерик >> Шопен - Вальсы и камерная музыка")),
                        ProcessingStatus.FAILURE,
                        null,
                        null));

        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        Artifact errorArtifact = artifactRepository.findAll()
                .stream()
                .filter(a -> a.getTitle().equals("Шопен - Вальсы и камерная музыка"))
                .findFirst()
                .orElseThrow();

        trackRepository.deleteAll(trackRepository.findAllByArtifact(errorArtifact));

        service.executeProcessor(ProcessorType.CLASSICS_VALIDATOR);
        ProcessInfo pi2 = service.getProcessInfo();

        log.info("Processing info: " + service.getProcessInfo());
        assertThat(pi2.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
    }

    @Test
    @Order(3)
    void testFail() {
        Artifact artifact = artifactRepository.findById(1L).orElseThrow();
        artifact.setTitle(artifact.getTitle() + "(changed)");
        artifactRepository.save(artifact);

        service.executeProcessor(ProcessorType.CLASSICS_VALIDATOR);

        log.info("Processing info: " + service.getProcessInfo());
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
    }
}
