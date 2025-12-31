package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.generator.DataGenerator;
import com.romanpulov.odeonwss.generator.FileTreeGenerator;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.MediaFileValidator;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateDVMusicTest {
    private static final Logger log = Logger.getLogger(ServiceProcessValidateDVMusicTest.class.getSimpleName());
    public static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MUSIC_VALIDATOR;

    @Value("${test.data.path}")
    String testDataPath;

    private enum TestFolder {
        TF_SERVICE_PROCESS_VALIDATE_DV_MUSIC_TEST_OK,
        TF_SERVICE_PROCESS_VALIDATE_DV_MUSIC_TEST_WITH_FOLDERS
    }

    private Map<TestFolder, Path> tempDirs;

    @BeforeAll
    public void setup() throws Exception {
        log.info("Before all");

        tempDirs = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_MUSIC_TEST_OK),
                this.testDataPath,
                """
{
  "Beautiful Voices 1": {
    "Beautiful Voices 1.mkv": "sample_1280x720_with_chapters.mkv",
    "tracks.txt": "sample.txt"
  },
  "The Cure - Picture Show 1991": {
    "The Cure - Picture Show 1991.mp4": "sample_MP4_480_1_5MG.mp4"
  },
  "Tori Amos - Fade to Red 2006": {
    "Tori Amos - Fade to Red Disk 1 2006.mkv": "sample_1280x720_with_chapters.mkv",
    "Tori Amos - Fade to Red Disk 2 2006.mkv": "sample_1280x720_600.mkv"
  }
}
                """
        );

        FileTreeGenerator.generateFromJSON(
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_MUSIC_TEST_WITH_FOLDERS),
                this.testDataPath,
                """
{
    "Aerosmith": {
        "2004 Honkin'On Bobo": {
            "01 - Road Runner.mp3": "sample_mp3_1.mp3",
            "02 - Shame, Shame, Shame.mp3": "sample_mp3_1.mp3"
        }
    },
    "Kosheen": {
        "2004 Kokopelli": {
            "01 - Wasting My Time.mp3": "sample_mp3_1.mp3"
        },
        "2007 Damage": {
            "01 - Damage.mp3": "sample_mp3_2.mp3",
            "02 - Overkill.mp3": "sample_mp3_2.mp3"
        }
    },
    "Various Artists": {
        "2000 Rock N' Roll Fantastic": {
            "001 - Simple Minds - Gloria.MP3": "sample_mp3_3.mp3"
        }
    }
}
                        """
        );

    }

    @AfterAll
    public void teardown() {
        log.info("After all");
        FileTreeGenerator.deleteTempFiles(tempDirs.values());
    }

    @Autowired
    DataGenerator dataGenerator;

    private ArtifactType artifactType;

    @Autowired
    ProcessService service;

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

    private ProcessInfo executeProcessorOk() {
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_MUSIC_TEST_OK).toString());
        return service.getProcessInfo();
    }

    private void internalPrepareImported() {
        String json =
                """
{
    "artists": [
        {"artistType": "A", "artistName": "The Cure"},
        {"artistType": "A", "artistName": "Tori Amos"},
        {"artistType": "A", "artistName": "Various Artists"},
        {"artistType": "A", "artistName": "A-HA"}
    ],
    "artifacts": [
        {"artifactType": { "id": 201 }, "artist": {"artistName": "The Cure"}, "title": "The Cure - Picture Show 1991", "duration": 0 },
        {"artifactType": { "id": 201 }, "artist": {"artistName": "Tori Amos"}, "title": "Tori Amos - Fade to Red 2006", "duration": 0 },
        {"artifactType": { "id": 201 }, "artist": {"artistName": "Various Artists"}, "title": "Beautiful Voices 1", "duration": 0 },
        {"artifactType": { "id": 201 }, "artist": {"artistName": "A-HA"}, "title": "A-HA - Ending On A High Note The Final Concert 2010", "duration": 0 },
        {"artifactType": { "id": 201 }, "artist": {"artistName": "A-HA"}, "title": "A-HA - Headlines And Deadlines The Hits Of A-HA 1991", "duration": 0 }
    ],
    "mediaFiles": [
        {"artifactTitle": "The Cure - Picture Show 1991", "name": "The Cure - Picture Show 1991.mp4" },
        {"artifactTitle": "Tori Amos - Fade to Red 2006", "name": "Tori Amos - Fade to Red Disk 1 2006.mkv" },
        {"artifactTitle": "Tori Amos - Fade to Red 2006", "name": "Tori Amos - Fade to Red Disk 2 2006.mkv" },
        {"artifactTitle": "Beautiful Voices 1", "name": "Beautiful Voices 1.mkv"},
        {"artifactTitle": "A-HA - Ending On A High Note The Final Concert 2010", "name": "A1.mkv"},
        {"artifactTitle": "A-HA - Headlines And Deadlines The Hits Of A-HA 1991", "name": "A2.mkv"}
    ],
    "tracks": [
        {"artifact": { "title": "Tori Amos - Fade to Red 2006"}, "title": "Tori Amos - Fade To Red 1", "mediaFiles": [
                {"name": "Tori Amos - Fade to Red Disk 1 2006.mkv"}
            ]
        },
        {"artifact": { "title": "Tori Amos - Fade to Red 2006"}, "title": "Tori Amos - Fade To Red 2", "mediaFiles": [
                {"name": "Tori Amos - Fade to Red Disk 2 2006.mkv"}
            ]
        },
        { "artifact": {"title": "The Cure - Picture Show 1991"}, "title": "Close To Me", "mediaFiles": [
                {"name": "The Cure - Picture Show 1991.mp4"}
            ], "num": 1
        },
        { "artifact": {"title": "Beautiful Voices 1"}, "title": "Beautiful Voices 1", "mediaFiles": [
                {"name": "Beautiful Voices 1.mkv"}
            ]
        }
    ]
}
                """;

        dataGenerator.generateFromJSON(json);
        /*
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

         */
    }

    private void internalPrepareExisting() {
        String json =
                """
{
    "artists": [
        {"artistType": "A", "artistName": "The Cure"},
        {"artistType": "A", "artistName": "Tori Amos"},
        {"artistType": "A", "artistName": "Various Artists"}
    ],
    "artifacts": [
        {"artifactType": { "id": 201 }, "artist": {"artistName": "The Cure"}, "title": "The Cure - Picture Show 1991", "duration": 31 },
        {"artifactType": { "id": 201 }, "artist": {"artistName": "Tori Amos"}, "title": "Tori Amos - Fade to Red 2006", "duration": 66 },
        {"artifactType": { "id": 201 }, "artist": {"artistName": "Various Artists"}, "title": "Beautiful Voices 1", "duration": 28 }
    ],
    "mediaFiles": [
        {"artifactTitle": "The Cure - Picture Show 1991", "name": "The Cure - Picture Show 1991.mp4" },
        {"artifactTitle": "Tori Amos - Fade to Red 2006", "name": "Tori Amos - Fade to Red Disk 1 2006.mkv" },
        {"artifactTitle": "Tori Amos - Fade to Red 2006", "name": "Tori Amos - Fade to Red Disk 2 2006.mkv" },
        {"artifactTitle": "Beautiful Voices 1", "name": "Beautiful Voices 1.mkv"}
    ],
    "tracks": [
        {"artifact": { "title": "Tori Amos - Fade to Red 2006"}, "title": "Tori Amos - Fade To Red 1", "mediaFiles": [
                {"name": "Tori Amos - Fade to Red Disk 1 2006.mkv"}
            ], "duration": 28
        },
        {"artifact": { "title": "Tori Amos - Fade to Red 2006"}, "title": "Tori Amos - Fade To Red 2", "mediaFiles": [
                {"name": "Tori Amos - Fade to Red Disk 2 2006.mkv"}
            ], "duration": 38
        },
        { "artifact": {"title": "The Cure - Picture Show 1991"}, "title": "Close To Me", "mediaFiles": [
                {"name": "The Cure - Picture Show 1991.mp4"}
            ], "duration": 31
        },
        { "artifact": {"title": "Beautiful Voices 1"}, "title": "Beautiful Voices 1", "mediaFiles": [
                {"name": "Beautiful Voices 1.mkv"}
            ], "duration": 28
        }
    ]
}
                """;

        dataGenerator.generateFromJSON(json);

        service.executeProcessor(
                ProcessorType.DV_MUSIC_MEDIA_LOADER,
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_MUSIC_TEST_OK).toString()
        );

        /*
        internalPrepareImported();
        DbManagerService.loadOrPrepare(databaseConfiguration,
                DbManagerService.DbType.DB_ARTISTS_DV_MUSIC_MEDIA_EXISTING,
                () ->
                    artifactRepository.getAllByArtifactType(artifactType)
                    .forEach(artifact -> {
                        if (!EXISTING_ARTIFACT_TITLES.contains(artifact.getTitle())) {
                            artifactRepository.delete(artifact);
                        } else {
                            Track firstTrack = trackRepository.findAllByArtifact(artifact).get(0);
                            firstTrack.setDuration(artifact.getDuration());
                            trackRepository.save(firstTrack);
                        }
                    }
                ));

         */
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepareImported() throws Exception {
        this.artifactType = artifactTypeRepository.getWithDVMusic();
        internalPrepareExisting();
    }

    @Test
    @Order(2)
    @Rollback(false)
    @Sql({"/schema.sql", "/data.sql"})
    void testValidateImportedShouldFail() throws Exception {
        this.internalPrepareImported();
        ProcessInfo pi = executeProcessorOk();
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

        assertThat(processDetails.get(1).getInfo().getMessage()).isEqualTo("Artifacts not in files");
        assertThat(processDetails.get(1).getInfo().getItems())
                .contains("A-HA - Ending On A High Note The Final Concert 2010", "A-HA - Headlines And Deadlines The Hits Of A-HA 1991");

        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
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
    void validateOk() throws Exception {
        this.internalPrepareExisting();
        ProcessInfo pi = executeProcessorOk();
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
                        ProcessDetailInfo.fromMessage("Artifacts validated"),
                        ProcessingStatus.INFO,
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
                        ProcessDetailInfo.fromMessage("Media files dimensions validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(9)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files size mismatch validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(10)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Monotonically increasing track numbers validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(11)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact tracks duration validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(12)).isEqualTo(
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
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_MUSIC_TEST_WITH_FOLDERS).toString());
        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(processDetails.get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage(
                                "Expected file, found: " + Path.of(
                                        tempDirs.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_MUSIC_TEST_WITH_FOLDERS).toString(),
                                        "Aerosmith",
                                        "2004 Honkin'On Bobo"
                                )),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(2).getInfo().getMessage()).isEqualTo(
                "Artifacts not in files");

        assertThat(pi.getProcessDetails().get(3)).isEqualTo(
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
    void testNewArtifactWithoutArtistShouldFail() throws Exception {
        this.internalPrepareExisting();
        Artifact artifact = (new EntityArtifactBuilder())
                .withArtifactType(artifactType)
                .withTitle("Artifact no artist")
                .build();
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessorOk();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(1)).isEqualTo(
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
    void testNewArtifactInDbShouldFail() throws Exception {
        this.internalPrepareExisting();
        Artist artist = artistRepository.findAll().iterator().next();
        assertThat(artist).isNotNull();

        Artifact artifact = (new EntityArtifactBuilder())
                .withArtifactType(artifactType)
                .withArtist(artist)
                .withTitle("New Artifact")
                .build();
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessorOk();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(1)).isEqualTo(
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
    void testNewArtifactInFilesShouldFail() throws Exception {
        this.internalPrepareExisting();
        Artifact artifact = artifactRepository.findAll().getFirst();
        artifactRepository.delete(artifact);

        ProcessInfo pi = executeProcessorOk();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(1)).isEqualTo(
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
    void testArtifactWithoutTracksShouldFail() throws Exception {
        this.internalPrepareExisting();
        Artifact artifact = artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Beautiful Voices 1"))
                .findFirst().orElseThrow();
        assertThat(artifact).isNotNull();
        var tracks = trackRepository.findAllByArtifact(artifact);
        trackRepository.deleteAll(tracks);

        ProcessInfo pi = executeProcessorOk();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        var pd = pi.getProcessDetails();
        assertThat(pd.get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "No tracks for artifact",
                                List.of("Beautiful Voices 1")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(17)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactWithoutMediaFilesShouldFail() throws Exception {
        this.internalPrepareExisting();
        Artifact artifact = artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("The Cure - Picture Show 1991"))
                .findFirst().orElseThrow();
        assertThat(artifact).isNotNull();

        var mediaFiles = mediaFileRepository.findAllByArtifactId(artifact.getId());
        assertThat(mediaFiles.isEmpty()).isFalse();
        mediaFileRepository.deleteAll(mediaFiles);

        ProcessInfo pi = executeProcessorOk();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        var pd = pi.getProcessDetails();
        assertThat(pd.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "No media files for artifact",
                                List.of("The Cure - Picture Show 1991")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(18)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewFileInDbShouldFail() throws Exception {
        this.internalPrepareExisting();
        Artifact artifact = artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Beautiful Voices 1"))
                .findFirst().orElseThrow();
        Track track = trackRepository
                .findByIdWithMediaFiles(artifact.getTracks().getFirst().getId())
                .orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("New music file.mkv");
        mediaFile.setFormat("MKV");
        mediaFile.setSize(150L);
        mediaFile.setBitrate(2235L);
        mediaFileRepository.save(mediaFile);

        track.getMediaFiles().add(mediaFile);
        trackRepository.save(track);

        ProcessInfo pi = executeProcessorOk();
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
    @Order(19)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewFileInFilesShouldFail() throws Exception {
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

        ProcessInfo pi = executeProcessorOk();
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
    @Order(20)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactFileInDbShouldFail() throws Exception {
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

        ProcessInfo pi = executeProcessorOk();
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
    @Order(21)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactFileInFilesShouldFail() throws Exception {
        this.internalPrepareExisting();
        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("Tori Amos - Fade to Red Disk 1 2006.mkv"))
                .findFirst()
                .orElseThrow();
        mediaFile.setArtifact(null);
        mediaFileRepository.save(mediaFile);

        ProcessInfo pi = executeProcessorOk();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact media files not in database",
                                List.of("Tori Amos - Fade to Red Disk 1 2006.mkv")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(22)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileEmptyBitrateShouldFail() throws Exception {
        this.internalPrepareExisting();
        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("The Cure - Picture Show 1991.mp4"))
                .findFirst()
                .orElseThrow();
        mediaFile.setBitrate(0L);
        mediaFileRepository.save(mediaFile);

        ProcessInfo pi = executeProcessorOk();
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
    @Order(23)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileSizeDifferentShouldFail() throws Exception {
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

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(9)).isEqualTo(
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
    @Order(24)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactMediaFileSizeDifferentShouldFail() throws Exception {
        this.internalPrepareExisting();

        Artifact artifact = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("The Cure - Picture Show 1991"))
                .findFirst().orElseThrow();

        artifact.setSize(Optional.ofNullable(artifact.getSize()).orElse(0L) + 530);
        artifactRepository.save(artifact);
        log.info("Saved artifact: " + artifact);

        executeProcessorOk();

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
    @Order(25)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactMediaFileDurationDifferentShouldFail() throws Exception {
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

        executeProcessorOk();

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

    @Test
    @Order(26)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactTrackDurationDifferentShouldFail() throws Exception {
        this.internalPrepareExisting();

        Track track = trackRepository
                .getTracksByArtifactType(artifactType)
                .stream()
                .filter(t -> t.getTitle().equals("Close To Me"))
                .findFirst()
                .orElseThrow();
        Artifact artifact = artifactRepository
                .findById(track.getArtifact().getId())
                .orElseThrow();

        track.setDuration(Optional.ofNullable(track.getDuration()).orElse(0L) + 5);
        trackRepository.save(track);
        log.info("Saved track: " + track);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(11)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact duration does not match tracks duration",
                                List.of(artifact.getTitle())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(27)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileMissingBitrateShouldFail() throws Exception {
        this.internalPrepareExisting();

        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(artifactType)
                .stream()
                .findFirst()
                .orElseThrow();
        mediaFile.setBitrate(null);
        mediaFileRepository.save(mediaFile);
        log.info("Saved media file: " + mediaFile);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(7)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files with empty bitrate",
                                List.of(MediaFileValidator.DELIMITER_FORMAT.formatted(mediaFile.getArtifact().getTitle(), mediaFile.getName()))),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(28)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileMissingWidthShouldFail() throws Exception {
        this.internalPrepareExisting();

        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(artifactType)
                .stream()
                .findFirst()
                .orElseThrow();
        mediaFile.setWidth(null);
        mediaFileRepository.save(mediaFile);
        log.info("Saved media file: " + mediaFile);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(8)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files with empty dimensions",
                                List.of(MediaFileValidator.DELIMITER_FORMAT.formatted(mediaFile.getArtifact().getTitle(), mediaFile.getName()))),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(29)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileMissingHeightShouldFail() throws Exception {
        this.internalPrepareExisting();

        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(artifactType)
                .stream()
                .findFirst()
                .orElseThrow();
        mediaFile.setHeight(null);
        mediaFileRepository.save(mediaFile);
        log.info("Saved media file: " + mediaFile);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(8)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files with empty dimensions",
                                List.of(MediaFileValidator.DELIMITER_FORMAT.formatted(mediaFile.getArtifact().getTitle(), mediaFile.getName()))),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }
}
