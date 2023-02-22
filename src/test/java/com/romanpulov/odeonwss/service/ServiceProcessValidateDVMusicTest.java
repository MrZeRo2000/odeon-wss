package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.model.ProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import com.romanpulov.odeonwss.service.processor.model.ProcessDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIf(value = "${full.tests.disabled}", loadContext = true)
public class ServiceProcessValidateDVMusicTest {
    private static final Logger log = Logger.getLogger(ServiceProcessValidateDVMusicTest.class.getSimpleName());
    private static final ArtifactType ARTIFACT_TYPE = ArtifactType.withDVMusic();
    private static final Set<String> EXISTING_ARTIFACT_TITLES = Set.of(
            "Beautiful Voices 1", "The Cure - Picture Show 1991", "Tori Amos - Fade to Red 2006");
    public static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MUSIC_VALIDATOR;

    @Autowired
    ProcessService service;

    @Autowired
    AppConfiguration appConfiguration;

    @Autowired
    private ArtifactRepository artifactRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;
    @Autowired
    private CompositionRepository compositionRepository;
    @Autowired
    private MediaFileRepository mediaFileRepository;

    private ProcessInfo executeProcessor() {
        service.executeProcessor(PROCESSOR_TYPE);
        return service.getProcessInfo();
    }

    private void internalPrepareImported() {
        DbManagerService.loadOrPrepare(appConfiguration, DbManagerService.DbType.DB_ARTISTS_DV_MUSIC_MEDIA, () -> {
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
        DbManagerService.loadOrPrepare(appConfiguration, DbManagerService.DbType.DB_ARTISTS_DV_MUSIC_MEDIA_EXISTING, () -> {
            artifactRepository.getAllByArtifactType(ARTIFACT_TYPE)
                    .forEach(artifact -> {
                        if (!EXISTING_ARTIFACT_TITLES.contains(artifact.getTitle())) {
                            artifactRepository.delete(artifact);
                        }
                    });
        });
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepareImported() {
        internalPrepareExisting();
    }

    @Test
    @Order(2)
    @Rollback(false)
    @Sql({"/schema.sql", "/data.sql"})
    void testValidateImportedShouldFail() {
        this.internalPrepareImported();
        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProgressDetails();
        log.info("Processing info: " + service.getProcessInfo());
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());

        assertThat(processDetails.get(0)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Started Video music validator", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(1).getInfo().getMessage()).isEqualTo("Media files with empty size");
        assertThat(processDetails.get(1).getInfo().getItems())
                .contains("Beauty In Darkness Vol 5.mkv", "Iron Maiden.mkv");

        assertThat(processDetails.get(2).getInfo().getMessage()).isEqualTo("Artifacts not in files");
        assertThat(processDetails.get(2).getInfo().getItems())
                .contains("Beauty In Darkness Vol.5", "Scorpions - Acoustica (Live in Lisboa) 2001");

        assertThat(pi.getProgressDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Task status", new ArrayList<>()),
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
        assertThat(pi.getProgressDetails().get(0)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Started Video music validator", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProgressDetails().get(1)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Media files size validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProgressDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Artifacts validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProgressDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Media files validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProgressDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Artifact media files validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProgressDetails().get(5)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Task status", new ArrayList<>()),
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

        List<ProcessDetail> processDetails = pi.getProgressDetails();
        assertThat(processDetails.get(1)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo(
                                "Expected file, found: ..\\odeon-test-data\\ok\\MP3 Music\\Aerosmith\\2004 Honkin'On Bobo",
                                new ArrayList<>()),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(pi.getProgressDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Media files size validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProgressDetails().get(3).getInfo().getMessage()).isEqualTo(
                "Artifacts not in files");

        assertThat(pi.getProgressDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo("Task status", new ArrayList<>()),
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
                .withArtifactType(ARTIFACT_TYPE)
                .withTitle("Artifact no artist")
                .build();
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProgressDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo(
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
                .withArtifactType(ARTIFACT_TYPE)
                .withArtist(artist)
                .withTitle("New Artifact")
                .build();
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProgressDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo(
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
        assertThat(pi.getProgressDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo(
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
        Artifact artifact = artifactRepository.getAllByArtifactTypeWithCompositions(ARTIFACT_TYPE)
                .stream()
                .filter(a -> a.getTitle().equals("Beautiful Voices 1"))
                .findFirst().orElseThrow();
        Composition composition = compositionRepository
                .findByIdWithMediaFiles(artifact.getCompositions().get(0).getId())
                .orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("New music file.mkv");
        mediaFile.setFormat("MKV");
        mediaFile.setSize(150L);
        mediaFile.setBitrate(2235L);
        mediaFileRepository.save(mediaFile);

        composition.getMediaFiles().add(mediaFile);
        compositionRepository.save(composition);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProgressDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo(
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
                .findAllByArtifact(artifact)
                .stream()
                .filter(m -> m.getName().equals("Tori Amos - Fade to Red Disk 2 2006.mkv"))
                .findFirst()
                .orElseThrow();

        compositionRepository.findAllByArtifact(artifact).forEach(c -> {
            Composition composition = compositionRepository.findByIdWithMediaFiles(c.getId()).orElseThrow();
            if (composition.getMediaFiles().contains(mediaFile)) {
                compositionRepository.delete(composition);
            }
        });
        mediaFile.setArtifact(null);
        mediaFileRepository.delete(mediaFile);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProgressDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo(
                                "Media files not in database",
                                List.of("Tori Amos - Fade to Red 2006 >> Tori Amos - Fade to Red Disk 2 2006.mkv")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(pi.getProgressDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo(
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
        Artifact artifact = artifactRepository.getAllByArtifactType(ARTIFACT_TYPE)
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

        assertThat(pi.getProgressDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo(
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
        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(ARTIFACT_TYPE)
                .stream()
                .filter(m -> m.getName().equals("The Cure - Picture Show 1991.mp4"))
                .findFirst()
                .orElseThrow();
        mediaFile.setArtifact(null);
        mediaFileRepository.save(mediaFile);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProgressDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        new ProcessDetail.ProcessInfo(
                                "Artifact media files not in database",
                                List.of("The Cure - Picture Show 1991.mp4")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

}
