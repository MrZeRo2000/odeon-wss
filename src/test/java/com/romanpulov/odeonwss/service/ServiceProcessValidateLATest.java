package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import com.romanpulov.odeonwss.service.processor.model.ProgressDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateLATest {
    private static final List<String> TEST_ARTISTS =
            List.of(
                    "Evanescence",
                    "Pink Floyd",
                    "Therapy",
                    "Tori Amos",
                    "Abigail Williams",
                    "Agua De Annique",
                    "Christina Aguilera");

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ProcessService service;

    @Autowired
    EntityManager em;

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private AppConfiguration appConfiguration;

    private void prepareInternal() {
        DbManagerService.loadOrPrepare(appConfiguration, DbManagerService.DbType.DB_LOADED_LA, () -> {
            TEST_ARTISTS
                    .forEach(s -> artistRepository.save(
                            new EntityArtistBuilder().withType(ArtistType.ARTIST).withName(s).build()
                    ));

            service.executeProcessor(ProcessorType.LA_LOADER, null);
            assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        });
    }

    private ProcessInfo executeProcessor() {
        service.executeProcessor(ProcessorType.LA_VALIDATOR);
        return service.getProcessInfo();
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testEmptyShouldFail() {
        TEST_ARTISTS
                .forEach(s -> artistRepository.save(
                        new EntityArtistBuilder().withType(ArtistType.ARTIST).withName(s).build()
                ));

        service.executeProcessor(ProcessorType.LA_VALIDATOR, null);
        ProcessInfo pi = service.getProcessInfo();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(progressDetails.get(0)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Started LA Validator", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(1)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artists not in database or have no artifacts and compositions",
                                TEST_ARTISTS.stream().sorted().collect(Collectors.toList())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(progressDetails.get(2)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(2)
    void testLoad() {
        service.executeProcessor(ProcessorType.LA_LOADER, null);
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
    }

    @Test
    @Order(3)
    void testOk() throws Exception {
        service.executeProcessor(ProcessorType.LA_VALIDATOR);
        ProcessInfo pi = service.getProcessInfo();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        int id = 0;
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Started LA Validator", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifacts validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Media files validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifact media files validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );
    }

    private String getErrorStringFromCompositionId(long id) {
        Composition composition = compositionRepository.findByIdWithMediaFiles(1L).orElseThrow();
        Artifact artifact = artifactRepository.findById(composition.getArtifact().getId()).orElseThrow();
        assert artifact.getArtist() != null;
        Artist artist = artistRepository.findById(artifact.getArtist().getId()).orElseThrow();
        return artist.getName() + " >> " +
               artifact.getYear() + " " + artifact.getTitle() + " >> " +
               composition.getMediaFiles().stream().findFirst().orElseThrow().getName();
    }

    @Test
    @Order(4)
    @Transactional
    void testNoCompositionMediaFileShouldFail() throws Exception {
        String errorString = getErrorStringFromCompositionId(1L);

        em.createNativeQuery("delete from compositions_media_files WHERE comp_id = 1").executeUpdate();
        service.executeProcessor(ProcessorType.LA_VALIDATOR);
        ProcessInfo pi = service.getProcessInfo();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        int id = 0;
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Started LA Validator", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifacts validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Media files not in database",
                                List.of(errorString)),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );


        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(5)
    @Transactional
    void testNoCompositionShouldFail() throws Exception {
        String errorString = getErrorStringFromCompositionId(1L);

        em.createNativeQuery("delete from compositions WHERE comp_id = 1").executeUpdate();
        service.executeProcessor(ProcessorType.LA_VALIDATOR);
        ProcessInfo pi = service.getProcessInfo();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        int id = 0;

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Started LA Validator", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifacts validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Media files not in database",
                                List.of(errorString)),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(6)
    void testOkAgain() throws Exception {
        service.executeProcessor(ProcessorType.LA_VALIDATOR);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(11)
    @Sql({"/schema.sql", "/data.sql"})
    void validateOk() {
        this.prepareInternal();
        ProcessInfo pi = executeProcessor();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        int id = 0;
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Started LA Validator", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifacts validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Media files validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifact media files validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );
    }

    @Test
    @Order(12)
    @Sql({"/schema.sql", "/data.sql"})
    void containsFilesShouldFail() {
        this.prepareInternal();
        service.executeProcessor(ProcessorType.LA_VALIDATOR, "../odeon-test-data/ok/Lossless/Therapy/1993 Nurse");
        ProcessInfo pi = service.getProcessInfo();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        assertThat(progressDetails.get(1).getInfo().getMessage()).contains("Expected directory, found");
        assertThat(progressDetails.get(2)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artists not in files or have no artifacts and compositions",
                                TEST_ARTISTS.stream().sorted().collect(Collectors.toList())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }
}
