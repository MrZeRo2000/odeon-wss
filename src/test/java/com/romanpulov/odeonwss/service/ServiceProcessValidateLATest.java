package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
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
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateLATest {
    private static final ArtifactType ARTIFACT_TYPE = ArtifactType.withLA();
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
    private MediaFileRepository mediaFileRepository;

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

    @Test
    @Order(14)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactInDbShouldFail() {
        this.prepareInternal();

        Artist artist = artistRepository.findAll().iterator().next();
        assertThat(artist).isNotNull();

        Artifact artifact = (new EntityArtifactBuilder())
                .withArtifactType(ARTIFACT_TYPE)
                .withArtist(artist)
                .withTitle("New Artifact")
                .withYear(2000L)
                .build();
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        int id = 1;
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artifacts not in files",
                                List.of(artist.getName() + " >> 2000 New Artifact")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(15)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactInFilesShouldFail() {
        this.prepareInternal();
        Artifact artifact = artifactRepository
                .findAll()
                .stream()
                .filter(a -> a.getTitle().equals("Origin"))
                .findFirst()
                .orElseThrow();
        artifactRepository.delete(artifact);

        ProcessInfo pi = executeProcessor();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        int id = 1;
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artifacts not in database",
                                List.of("Evanescence >> 2000 Origin")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

    }

    @Test
    @Order(16)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewFileInDbShouldFail() {
        this.prepareInternal();
        Artifact artifact = artifactRepository.getAllByArtifactTypeWithCompositions(ARTIFACT_TYPE)
                .stream()
                .filter(a -> a.getTitle().equals("Evanescence") && !Objects.isNull(a.getYear()) && a.getYear().equals(2011L))
                .findFirst().orElseThrow();
        Composition composition = compositionRepository
                .findByIdWithMediaFiles(artifact.getCompositions().get(0).getId())
                .orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("New music file.flac");
        mediaFile.setFormat("FLAC");
        mediaFile.setSize(29883733L);
        mediaFile.setBitrate(1048L);
        mediaFileRepository.save(mediaFile);

        composition.getMediaFiles().add(mediaFile);
        compositionRepository.save(composition);

        ProcessInfo pi = executeProcessor();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(progressDetails.get(3)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Media files not in files",
                                List.of("Evanescence >> 2011 Evanescence >> " + mediaFile.getName())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(17)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewFileInFilesShouldFail() {
        this.prepareInternal();
        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(ARTIFACT_TYPE)
                .stream()
                .filter(m -> m.getName().equals("16 - Secret Door.flac"))
                .findFirst()
                .orElseThrow();
        mediaFileRepository.delete(mediaFile);

        ProcessInfo pi = executeProcessor();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(progressDetails.get(3)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Media files not in database",
                                List.of("Evanescence >> 2011 Evanescence >> " + mediaFile.getName())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(progressDetails.get(4)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artifact media files not in database",
                                List.of("Evanescence >> 2011 Evanescence >> " + mediaFile.getName())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(18)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactFileInDbShouldFail() {
        this.prepareInternal();
        Artifact artifact = artifactRepository.getAllByArtifactType(ARTIFACT_TYPE)
                .stream()
                .filter(a -> a.getTitle().equals("In The Absence Of Light"))
                .findFirst().orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("Another media file.flac");
        mediaFile.setFormat("FLAC");
        mediaFile.setSize(160453L);
        mediaFile.setBitrate(987L);
        mediaFile.setArtifact(artifact);
        mediaFileRepository.save(mediaFile);

        ProcessInfo pi = executeProcessor();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(progressDetails.get(4)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artifact media files not in files",
                                List.of("Abigail Williams >> 2010 In The Absence Of Light >> Another media file.flac")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(19)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactFileInFilesShouldFail() {
        this.prepareInternal();
        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(ARTIFACT_TYPE)
                .stream()
                .filter(m -> m.getName().equals("01 Hope The Great Betrayal.flac"))
                .findFirst()
                .orElseThrow();
        mediaFile.setArtifact(null);
        mediaFileRepository.save(mediaFile);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProgressDetails().get(4)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artifact media files not in database",
                                List.of("Abigail Williams >> 2010 In The Absence Of Light >> 01 Hope The Great Betrayal.flac")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }
}
