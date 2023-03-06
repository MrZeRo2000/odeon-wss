package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIf(value = "${full.tests.disabled}", loadContext = true)
public class ServiceProcessMDBImportClassicsTest {

    private static final Logger log = Logger.getLogger(ServiceProcessMDBImportClassicsTest.class.getSimpleName());
    public static final ProcessorType PROCESSOR_TYPE = ProcessorType.CLASSICS_IMPORTER;

    @Autowired
    ProcessService service;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    TrackRepository trackRepository;

    private ProcessInfo executeProcessor() {
        service.executeProcessor(PROCESSOR_TYPE);
        return service.getProcessInfo();
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testLoad() {
        ProcessInfo pi = executeProcessor();
        log.info("Processing info: " + service.getProcessInfo());
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        assertThat(processDetails.get(0)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Classics importer"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(1).getInfo().getMessage()).isEqualTo("Artists loaded");
        assertThat(processDetails.get(1).getRows()).isGreaterThan(0);

        assertThat(processDetails.get(2).getInfo().getMessage()).isEqualTo("Artist details imported");
        assertThat(processDetails.get(2).getRows()).isGreaterThan(0);

        assertThat(processDetails.get(3).getInfo().getMessage()).isEqualTo("Artist categories imported");
        assertThat(processDetails.get(3).getRows()).isGreaterThan(0);

        assertThat(processDetails.get(4).getInfo().getMessage()).isEqualTo("Artists cleansed");
        assertThat(processDetails.get(4).getRows()).isGreaterThan(0);

        assertThat(processDetails.get(5).getInfo().getMessage()).isEqualTo("Artifacts imported");
        assertThat(processDetails.get(5).getRows()).isGreaterThan(0);

        assertThat(processDetails.get(6)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );
    }

    @Test
    @Order(2)
    void testValidate() {
        assertThat(artistRepository.getAllByType(ArtistType.ARTIST).size()).isEqualTo(0);

        List<Artist> artists = artistRepository.getAllByType(ArtistType.CLASSICS);
        assertThat(artists.size()).isGreaterThan(0);
        assertThat(artists.stream().map(Artist::getMigrationId).collect(Collectors.toSet()).size()).isEqualTo(artists.size());

        assertThat(StreamSupport.stream(artistRepository.findAll().spliterator(), false).count()).isEqualTo(99);
        assertThat(artifactRepository.findAll().size()).isEqualTo(73);
        assertThat(StreamSupport.stream(trackRepository.findAll().spliterator(), false).count()).isEqualTo(157);
    }

    @Test
    @Order(3)
    void testRunSecondTime() {
        long artistCount = StreamSupport.stream(artistRepository.findAll().spliterator(), false).count();
        long artifactCount = artifactRepository.findAll().size();
        long trackCount = StreamSupport.stream(trackRepository.findAll().spliterator(), false).count();

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        assertThat(processDetails.get(1).getInfo().getMessage()).isEqualTo("Artists loaded");
        assertThat(processDetails.get(1).getRows()).isEqualTo(0);

        assertThat(processDetails.get(2).getInfo().getMessage()).isEqualTo("Artist details imported");
        assertThat(processDetails.get(2).getRows()).isEqualTo(0);

        assertThat(processDetails.get(3).getInfo().getMessage()).isEqualTo("Artist categories imported");
        assertThat(processDetails.get(3).getRows()).isEqualTo(0);

        assertThat(processDetails.get(4).getInfo().getMessage()).isEqualTo("Artists cleansed");
        assertThat(processDetails.get(4).getRows()).isEqualTo(0);

        assertThat(processDetails.get(5).getInfo().getMessage()).isEqualTo("Artifacts imported");
        assertThat(processDetails.get(5).getRows()).isEqualTo(0);

        assertThat(StreamSupport.stream(artistRepository.findAll().spliterator(), false).count()).isEqualTo(artistCount);
        assertThat(artifactRepository.findAll().size()).isEqualTo(artifactCount);
        assertThat(StreamSupport.stream(trackRepository.findAll().spliterator(), false).count()).isEqualTo(trackCount);
    }
}
