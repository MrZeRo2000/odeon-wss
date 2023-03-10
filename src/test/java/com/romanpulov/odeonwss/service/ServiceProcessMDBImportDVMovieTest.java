package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIf(value = "${full.tests.disabled}", loadContext = true)
public class ServiceProcessMDBImportDVMovieTest {

    public static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MOVIES_IMPORTER;

    private ArtifactType artifactType;

    @Autowired
    ProcessService service;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    TrackRepository trackRepository;

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
        this.artifactType = artifactTypeRepository.getWithDVMovies();

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

        assertThat(processDetails.get(4).getInfo().getMessage()).isEqualTo("Tracks imported");
        assertThat(processDetails.get(4).getRows()).isGreaterThan(0);
        assertThat(processDetails.get(4).getRows()).isEqualTo(processDetails.get(3).getRows());

        assertThat(processDetails.get(5).getInfo().getMessage()).isEqualTo("Products for tracks imported");
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
        int oldArtifacts = artifactRepository.getAllByArtifactType(artifactType).size();
        int oldTracks = trackRepository.getTracksByArtifactType(artifactType).size();
        int oldMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(artifactType).size();

        assertThat(oldArtifacts).isGreaterThan(0);
        assertThat(oldTracks).isGreaterThan(0);
        assertThat(oldMediaFiles).isGreaterThan(0);
        assertThat(oldTracks).isEqualTo(oldArtifacts);
        assertThat(oldMediaFiles).isGreaterThanOrEqualTo(oldTracks);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        assertThat(processDetails.get(1).getRows()).isEqualTo(0);
        assertThat(processDetails.get(2).getRows()).isEqualTo(0);
        assertThat(processDetails.get(3).getRows()).isEqualTo(0);
        assertThat(processDetails.get(4).getRows()).isEqualTo(0);
        assertThat(processDetails.get(5).getRows()).isEqualTo(0);
        assertThat(processDetails.get(6).getRows()).isEqualTo(0);

        int newArtifacts = artifactRepository.getAllByArtifactType(artifactType).size();
        int newTracks = trackRepository.getTracksByArtifactType(artifactType).size();
        int newMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(artifactType).size();

        assertThat(newArtifacts).isEqualTo(oldArtifacts);
        assertThat(newTracks).isEqualTo(oldTracks);
        assertThat(newMediaFiles).isEqualTo(oldMediaFiles);
        assertThat(newTracks).isEqualTo(oldTracks);
    }

    @Test
    @Order(3)
    void testImportDVMovieWithMediaFiles() {
        service.executeProcessor(ProcessorType.DV_MOVIES_MEDIA_LOADER);
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
    }

    @Test
    @Order(10)
    void testProductsForAll() {
        trackRepository.getTracksByArtifactType(artifactType).forEach(
                c -> {
                    Track track = trackRepository.findByIdWithProducts(c.getId()).orElseThrow();
                    assertThat(track.getDvProducts().size()).isEqualTo(1);
                }
        );
    }
}
