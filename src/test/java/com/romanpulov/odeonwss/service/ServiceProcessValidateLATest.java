package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.config.DatabaseConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(value = "test-06")
public class ServiceProcessValidateLATest {
    private static final List<String> TEST_ARTISTS =
            List.of(
                    "Evanescence",
                    "Pink Floyd",
                    "Therapy",
                    "Tori Amos",
                    "Abigail Williams",
                    "Agua De Annique",
                    "Christina Aguilera",
                    "The Sisters Of Mercy");

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ProcessService service;

    @Autowired
    EntityManager em;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private DatabaseConfiguration databaseConfiguration;

    private void prepareInternal() {
        DbManagerService.loadOrPrepare(databaseConfiguration, DbManagerService.DbType.DB_LOADED_LA, () -> {
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
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(processDetails.get(0)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started LA Validator"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artists not in database or have no artifacts and tracks",
                                TEST_ARTISTS.stream().sorted().collect(Collectors.toList())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(processDetails.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
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
    void testOk() {
        service.executeProcessor(ProcessorType.LA_VALIDATOR);
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started LA Validator"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files validated"),
                        ProcessingStatus.INFO,
                        null,
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

    private String getErrorStringFromTrackId(long id) {
        Track track = trackRepository.findByIdWithMediaFiles(id).orElseThrow();
        Artifact artifact = artifactRepository.findById(track.getArtifact().getId()).orElseThrow();
        assert artifact.getArtist() != null;
        Artist artist = artistRepository.findById(artifact.getArtist().getId()).orElseThrow();
        return artist.getName() + " >> " +
               artifact.getYear() + " " + artifact.getTitle() + " >> " +
               track.getMediaFiles().stream().findFirst().orElseThrow().getName();
    }

    @Test
    @Order(4)
    @Transactional
    void testNoTrackMediaFileShouldFail() {
        String errorString = getErrorStringFromTrackId(1L);

        em.createNativeQuery("delete from tracks_media_files WHERE trck_id = 1").executeUpdate();
        service.executeProcessor(ProcessorType.LA_VALIDATOR);
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started LA Validator"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
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
    void testNoTrackShouldFail() {
        String errorString = getErrorStringFromTrackId(1L);

        em.createNativeQuery("delete from tracks WHERE trck_id = 1").executeUpdate();
        service.executeProcessor(ProcessorType.LA_VALIDATOR);
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        int id = 0;

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started LA Validator"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in database",
                                List.of(errorString)),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(6)
    void testOkAgain() {
        service.executeProcessor(ProcessorType.LA_VALIDATOR);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(11)
    @Sql({"/schema.sql", "/data.sql"})
    void validateOk() {
        this.prepareInternal();
        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started LA Validator"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files validated"),
                        ProcessingStatus.INFO,
                        null,
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
    @Order(12)
    @Sql({"/schema.sql", "/data.sql"})
    void containsFilesShouldFail() {
        this.prepareInternal();
        service.executeProcessor(ProcessorType.LA_VALIDATOR, "../odeon-test-data/ok/Lossless/Therapy/1993 Nurse");
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(processDetails.get(1).getInfo().getMessage()).contains("Expected directory, found");
        assertThat(processDetails.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artists not in files or have no artifacts and tracks",
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
        ArtifactType artifactType = artifactTypeRepository.getWithLA();

        Artist artist = artistRepository.findAll().iterator().next();
        assertThat(artist).isNotNull();

        Artifact artifact = (new EntityArtifactBuilder())
                .withArtifactType(artifactType)
                .withArtist(artist)
                .withTitle("New Artifact")
                .withYear(2000L)
                .build();
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        int id = 1;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifacts not in files",
                                List.of(artist.getName() + " >> 2000 New Artifact")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
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
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        int id = 1;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifacts not in database",
                                List.of("Evanescence >> 2000 Origin")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
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
        ArtifactType artifactType = artifactTypeRepository.getWithLA();

        Artifact artifact = artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Evanescence") && !Objects.isNull(a.getYear()) && a.getYear().equals(2011L))
                .findFirst().orElseThrow();
        Track track = trackRepository
                .findByIdWithMediaFiles(artifact.getTracks().get(0).getId())
                .orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("New music file.flac");
        mediaFile.setFormat("FLAC");
        mediaFile.setSize(29883733L);
        mediaFile.setBitrate(1048L);
        mediaFileRepository.save(mediaFile);

        track.getMediaFiles().add(mediaFile);
        trackRepository.save(track);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
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
        ArtifactType artifactType = artifactTypeRepository.getWithLA();

        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("16 - Secret Door.flac"))
                .findFirst()
                .orElseThrow();
        mediaFileRepository.delete(mediaFile);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in database",
                                List.of("Evanescence >> 2011 Evanescence >> " + mediaFile.getName())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(processDetails.get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
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
        ArtifactType artifactType = artifactTypeRepository.getWithLA();

        Artifact artifact = artifactRepository.getAllByArtifactType(artifactType)
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
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
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
        ArtifactType artifactType = artifactTypeRepository.getWithLA();

        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("01 Hope The Great Betrayal.flac"))
                .findFirst()
                .orElseThrow();
        mediaFile.setArtifact(null);
        mediaFileRepository.save(mediaFile);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact media files not in database",
                                List.of("Abigail Williams >> 2010 In The Absence Of Light >> 01 Hope The Great Betrayal.flac")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }
}
