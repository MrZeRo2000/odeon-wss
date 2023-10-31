package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.config.DatabaseConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(value = "test-05")
public class ServiceProcessValidateDVMusicTest {
    private static final Logger log = Logger.getLogger(ServiceProcessValidateDVMusicTest.class.getSimpleName());
    private static final Set<String> EXISTING_ARTIFACT_TITLES = Set.of(
            "Beautiful Voices 1", "The Cure - Picture Show 1991", "Tori Amos - Fade to Red 2006");
    public static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MUSIC_VALIDATOR;

    private ArtifactType artifactType;

    @Autowired
    ProcessService service;

    @Autowired
    DatabaseConfiguration databaseConfiguration;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    private ProcessInfo executeProcessor() {
        service.executeProcessor(PROCESSOR_TYPE);
        return service.getProcessInfo();
    }

    private void internalPrepareImported() {
        DbManagerService.loadOrPrepare(databaseConfiguration, DbManagerService.DbType.DB_ARTISTS_DV_MUSIC_MEDIA, () -> {
            // load artists
            service.executeProcessor(ProcessorType.ARTISTS_IMPORTER);
            Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
            log.info("Artist Importer Processing info: " + service.getProcessInfo());

            // load dv music
            service.executeProcessor(ProcessorType.DV_MUSIC_IMPORTER);
            Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
            log.info("Music Importer Processing info: " + service.getProcessInfo());

            // update dv music media
            service.executeProcessor(ProcessorType.DV_MUSIC_MEDIA_LOADER);
            Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
            log.info("Music Media Loader Processing info: " + service.getProcessInfo());
        });
    }

    private void internalPrepareExisting() {
        internalPrepareImported();
        DbManagerService.loadOrPrepare(databaseConfiguration, DbManagerService.DbType.DB_ARTISTS_DV_MUSIC_MEDIA_EXISTING, () ->
                artifactRepository.getAllByArtifactType(artifactType)
                .forEach(artifact -> {
                    if (!EXISTING_ARTIFACT_TITLES.contains(artifact.getTitle())) {
                        artifactRepository.delete(artifact);
                    }
                }));
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepareImported() {
        this.artifactType = artifactTypeRepository.getWithDVMusic();
        internalPrepareExisting();
    }

    @Test
    @Order(2)
    @Rollback(false)
    @Sql({"/schema.sql", "/data.sql"})
    void testValidateImportedShouldFail() {
        this.internalPrepareImported();
        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        log.info("Processing info: " + service.getProcessInfo());
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());

        assertThat(processDetails.get(0)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Video music validator"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(1).getInfo().getMessage()).isEqualTo("Media files with empty size");
        assertThat(processDetails.get(1).getInfo().getItems())
                .contains("Beauty In Darkness Vol 5.mkv", "Iron Maiden.mkv");

        assertThat(processDetails.get(2).getInfo().getMessage()).isEqualTo("Artifacts not in files");
        assertThat(processDetails.get(2).getInfo().getItems())
                .contains("A-HA - Ending On A High Note The Final Concert 2010", "A-HA - Headlines And Deadlines The Hits Of A-HA 1991");

        assertThat(pi.getProcessDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

    }

    @Test
    @Order(11)
    @Sql({"/schema.sql", "/data.sql"})
    void validateOk() {
        this.internalPrepareExisting();
        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        assertThat(pi.getProcessDetails().get(0)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Video music validator"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files size validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files size validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(6)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files duration validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(7)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files bitrate validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(8)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files size mismatch validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(9)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Monotonically increasing track numbers validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(10)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );

    }

    @Test
    @Order(12)
    void testContainsFoldersShouldFail() {
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/ok/MP3 Music/");
        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(processDetails.get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage(
                                "Expected file, found: ..\\odeon-test-data\\ok\\MP3 Music\\Aerosmith\\2004 Honkin'On Bobo"),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files size validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(3).getInfo().getMessage()).isEqualTo(
                "Artifacts not in files");

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

    }

    @Test
    @Order(13)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactWithoutArtistShouldFail() {
        this.internalPrepareExisting();
        Artifact artifact = (new EntityArtifactBuilder())
                .withArtifactType(artifactType)
                .withTitle("Artifact no artist")
                .build();
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifacts without artists",
                                List.of("Artifact no artist")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(14)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactInDbShouldFail() {
        this.internalPrepareExisting();
        Artist artist = artistRepository.findAll().iterator().next();
        assertThat(artist).isNotNull();

        Artifact artifact = (new EntityArtifactBuilder())
                .withArtifactType(artifactType)
                .withArtist(artist)
                .withTitle("New Artifact")
                .build();
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifacts not in files",
                                List.of("New Artifact")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

    }

    @Test
    @Order(15)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactInFilesShouldFail() {
        this.internalPrepareExisting();
        Artifact artifact = artifactRepository.findAll().get(0);
        artifactRepository.delete(artifact);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifacts not in database",
                                List.of(artifact.getTitle())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(16)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewFileInDbShouldFail() {
        this.internalPrepareExisting();
        Artifact artifact = artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Beautiful Voices 1"))
                .findFirst().orElseThrow();
        Track track = trackRepository
                .findByIdWithMediaFiles(artifact.getTracks().get(0).getId())
                .orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("New music file.mkv");
        mediaFile.setFormat("MKV");
        mediaFile.setSize(150L);
        mediaFile.setBitrate(2235L);
        mediaFileRepository.save(mediaFile);

        track.getMediaFiles().add(mediaFile);
        trackRepository.save(track);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in files",
                                List.of(artifact.getTitle() + " >> " + mediaFile.getName())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

    }

    @Test
    @Order(17)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewFileInFilesShouldFail() {
        this.internalPrepareExisting();
        Artifact artifact = artifactRepository
                .findAll()
                .stream()
                .filter(a -> a.getTitle().equals("Tori Amos - Fade to Red 2006"))
                .findFirst()
                .orElseThrow();
        MediaFile mediaFile = mediaFileRepository
                .findAllByArtifactId(artifact.getId())
                .stream()
                .filter(m -> m.getName().equals("Tori Amos - Fade to Red Disk 2 2006.mkv"))
                .findFirst()
                .orElseThrow();

        trackRepository.findAllByArtifact(artifact).forEach(c -> {
            Track track = trackRepository.findByIdWithMediaFiles(c.getId()).orElseThrow();
            if (track.getMediaFiles().contains(mediaFile)) {
                trackRepository.delete(track);
            }
        });
        mediaFile.setArtifact(null);
        mediaFileRepository.delete(mediaFile);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in database",
                                List.of("Tori Amos - Fade to Red 2006 >> Tori Amos - Fade to Red Disk 2 2006.mkv")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact media files not in database",
                                List.of("Tori Amos - Fade to Red Disk 2 2006.mkv")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(18)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactFileInDbShouldFail() {
        this.internalPrepareExisting();
        Artifact artifact = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("The Cure - Picture Show 1991"))
                .findFirst().orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("Another media file.mkv");
        mediaFile.setFormat("MKV");
        mediaFile.setSize(160445L);
        mediaFile.setBitrate(2345L);
        mediaFile.setArtifact(artifact);
        mediaFileRepository.save(mediaFile);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact media files not in files",
                                List.of("Another media file.mkv")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(19)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactFileInFilesShouldFail() {
        this.internalPrepareExisting();
        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("The Cure - Picture Show 1991.mp4"))
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
                                List.of("The Cure - Picture Show 1991.mp4")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(20)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileEmptyBitrateShouldFail() {
        this.internalPrepareExisting();
        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("The Cure - Picture Show 1991.mp4"))
                .findFirst()
                .orElseThrow();
        mediaFile.setBitrate(0L);
        mediaFileRepository.save(mediaFile);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(7)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files with empty bitrate",
                                List.of("The Cure - Picture Show 1991 >> The Cure - Picture Show 1991.mp4")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(21)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileSizeDifferentShouldFail() {
        this.internalPrepareExisting();

        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("The Cure - Picture Show 1991.mp4"))
                .findFirst()
                .orElseThrow();
        mediaFile.setSize(mediaFile.getSize() + 500L);
        mediaFileRepository.save(mediaFile);
        log.info("Saved media file: " + mediaFile);

        Artifact artifact = mediaFile.getArtifact();
        artifact.setSize(Optional.ofNullable(artifact.getSize()).orElse(0L) + 500L);
        artifactRepository.save(artifact);

        service.executeProcessor(PROCESSOR_TYPE);

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(8)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files size mismatch",
                                List.of("The Cure - Picture Show 1991 >> The Cure - Picture Show 1991.mp4")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(22)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactMediaFileSizeDifferentShouldFail() {
        this.internalPrepareExisting();

        Artifact artifact = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("The Cure - Picture Show 1991"))
                .findFirst().orElseThrow();

        artifact.setSize(Optional.ofNullable(artifact.getSize()).orElse(0L) + 530);
        artifactRepository.save(artifact);
        log.info("Saved artifact: " + artifact);

        service.executeProcessor(PROCESSOR_TYPE);

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact size does not match media files size",
                                List.of(artifact.getTitle())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(23)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactMediaFileDurationDifferentShouldFail() {
        this.internalPrepareExisting();

        Artifact artifact = artifactRepository
                .getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("The Cure - Picture Show 1991"))
                .findFirst()
                .orElseThrow();
        artifact.setDuration(Optional.ofNullable(artifact.getDuration()).orElse(0L) + 11);
        artifactRepository.save(artifact);
        log.info("Saved artifact: " + artifact);

        service.executeProcessor(PROCESSOR_TYPE);

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(6)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact duration does not match media files duration",
                                List.of(artifact.getTitle())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }
}
