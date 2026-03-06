package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.generator.DataGenerator;
import com.romanpulov.odeonwss.generator.FileTreeGenerator;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
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
import java.util.Set;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateDVOtherTest {
    private final static List<String> DV_PRODUCT_NAMES = List.of(
            "Genesis - Together and Apart",
            "Michael Flatley - Lord of the Dance"
    );

    private static final Logger log = Logger.getLogger(ServiceProcessValidateDVOtherTest.class.getSimpleName());

    @Value("${test.data.path}")
    String testDataPath;

    private enum TestFolder {
        TF_SERVICE_PROCESS_VALIDATE_DV_OTHER_TEST_OK
    }

    private Map<TestFolder, Path> tempFolders;

    @BeforeAll
    public void setup() throws Exception {
        this.tempFolders = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_OTHER_TEST_OK),
                this.testDataPath,
                """
{
  "Genesis - Together and Apart": {
    "main_part.mkv": "sample_1280x720_with_chapters.mkv"
  },
  "Michael Flatley - Lord of the Dance": {
    "lord1.avi": "sample_AVI_480_750kB.avi"
  }
}
                     """
        );
    }

    @AfterAll
    public void teardown() {
        log.info("After all");
        FileTreeGenerator.deleteTempFiles(tempFolders.values());
    }

    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_OTHER_VALIDATOR;

    @Autowired
    DataGenerator dataGenerator;

    @Autowired
    ProcessService service;

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    private ArtifactType artifactType;

    @BeforeEach
    void beforeEach() {
        this.artifactType = artifactTypeRepository.getWithDVOther();
    }

    private void internalPrepare() {
        // prepare products
        dataGenerator.createProductsFromList(artifactType, DV_PRODUCT_NAMES);

        service.executeProcessor(
                ProcessorType.DV_OTHER_LOADER,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_OTHER_TEST_OK).toString());

        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        log.info("Other loader Processing info: " + service.getProcessInfo());
    }

    private void executeProcessorOk() {
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_OTHER_TEST_OK).toString());
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepare() {
        internalPrepare();
    }

    @Test
    @Order(2)
    void testValidateOk() {
        executeProcessorOk();
        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        assertThat(pi.getProcessDetails().get(0)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Other Validator"),
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
                        ProcessDetailInfo.fromMessage("Media files validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files size validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files duration validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(6)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files bitrate validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(7)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files dimensions validated"),
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
                        ProcessDetailInfo.fromMessage("Artifact tracks duration validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(11)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Products for tracks validated"),
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
    @Order(4)
    void testNewArtifactInDbShouldFail() {
        Artifact artifact = (new EntityArtifactBuilder())
                .withArtifactType(artifactType)
                .withTitle("New Artifact")
                .build();
        artifactRepository.save(artifact);
        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
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
    @Order(5)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactInFilesShouldFail() {
        this.internalPrepare();
        Artifact artifact = artifactRepository.findAll().getFirst();
        artifactRepository.delete(artifact);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
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
    @Order(6)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewFileInDbShouldFail() {
        this.internalPrepare();
        Artifact artifact = artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Genesis - Together and Apart"))
                .findFirst().orElseThrow();
        Track track = trackRepository
                .findByIdWithMediaFiles(artifact.getTracks().getFirst().getId())
                .orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("New media file.mkv");
        mediaFile.setFormat("MKV");
        mediaFile.setSize(100L);
        mediaFile.setBitrate(245L);
        mediaFileRepository.save(mediaFile);

        track.getMediaFiles().add(mediaFile);
        trackRepository.save(track);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
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
    @Order(7)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewFileInFilesShouldFail() {
        this.internalPrepare();
        Track track = trackRepository
                .findByIdWithMediaFiles(
                        trackRepository.getTracksByArtifactType(artifactType)
                        .stream()
                        .filter(c -> c.getTitle().equals("Genesis - Together and Apart"))
                        .findFirst()
                        .orElseThrow()
                        .getId()
                ).orElseThrow();
        track.getMediaFiles().removeIf(m -> m.getName().equals("main_part.mkv"));
        trackRepository.save(track);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in database",
                                List.of("Genesis - Together and Apart >> main_part.mkv")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(8)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactFileInDbShouldFail() {
        this.internalPrepare();
        Artifact artifact = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Genesis - Together and Apart"))
                .findFirst().orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("New media file.mkv");
        mediaFile.setFormat("MKV");
        mediaFile.setSize(100L);
        mediaFile.setBitrate(245L);
        mediaFile.setArtifact(artifact);
        mediaFileRepository.save(mediaFile);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact media files not in files",
                                List.of(mediaFile.getName())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(9)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactFileInFilesShouldFail() {
        this.internalPrepare();
        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("main_part.mkv"))
                .findFirst()
                .orElseThrow();
        mediaFile.setArtifact(null);
        mediaFileRepository.save(mediaFile);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "No media files for artifact",
                                List.of("Genesis - Together and Apart")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(10)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactWithoutTracksShouldFail() {
        this.internalPrepare();
        var artifact = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .filter(v -> v.getTitle().equals("Genesis - Together and Apart"))
                .findFirst()
                .orElseThrow();
        assertThat(artifact).isNotNull();
        var tracks = trackRepository.findAllByArtifact(artifact);
        trackRepository.deleteAll(tracks);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        var pd = pi.getProcessDetails();
        assertThat(pd.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "No tracks for artifact",
                                List.of("Genesis - Together and Apart")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(11)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactWithoutMediaFilesShouldFail() {
        this.internalPrepare();

        Artifact artifact = artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Genesis - Together and Apart"))
                .findFirst().orElseThrow();
        assertThat(artifact).isNotNull();

        var mediaFiles = mediaFileRepository.findAllByArtifactId(artifact.getId());
        assertThat(mediaFiles.isEmpty()).isFalse();
        mediaFileRepository.deleteAll(mediaFiles);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        var pd = pi.getProcessDetails();
        assertThat(pd.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "No media files for artifact",
                                List.of("Genesis - Together and Apart")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(12)
    @Sql({"/schema.sql", "/data.sql"})
    void testMissingProductForTrackShouldFail() {
        this.internalPrepare();

        //remove a product
        var track_to_remove = trackRepository.getTracksByArtifactType(artifactType)
                .stream()
                .filter(t -> t.getTitle().equals("Genesis - Together and Apart"))
                .findFirst().orElseThrow();

        track_to_remove.setDvProducts(Set.of());
        trackRepository.save(track_to_remove);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(11)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Tracks without product",
                                List.of("Genesis - Together and Apart >> Genesis - Together and Apart")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }


    @Test
    @Order(13)
    @Sql({"/schema.sql", "/data.sql"})
    void testMonotonicallyIncreasingTrackNumbersShouldFail() {
        this.internalPrepare();

        var track1 = trackRepository
                .getTracksByArtifactType(artifactType)
                .stream()
                .filter(t -> t.getTitle().equals("Genesis - Together and Apart"))
                .findFirst()
                .orElseThrow();
        assertThat(track1.getNum()).isEqualTo(1L);

        track1.setNum(3L);
        trackRepository.save(track1);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(9)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Track numbers for artifact not increasing monotonically",
                                List.of("Genesis - Together and Apart")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(14)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileSizeDifferentShouldFail() {
        this.internalPrepare();

        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(artifactType)
                .stream()
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

        assertThat(pi.getProcessDetails().get(8)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files size mismatch",
                                List.of(MediaFileValidator.DELIMITER_FORMAT.formatted(mediaFile.getArtifact().getTitle(), mediaFile.getName()))),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(15)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactMediaFileSizeDifferentShouldFail() {
        this.internalPrepare();

        Artifact artifact = artifactRepository
                .getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Genesis - Together and Apart"))
                .findFirst()
                .orElseThrow();
        artifact.setSize(Optional.ofNullable(artifact.getSize()).orElse(0L) + 130);
        artifactRepository.save(artifact);
        log.info("Saved artifact: " + artifact);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
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
    @Order(16)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactMediaFileDurationDifferentShouldFail() {
        this.internalPrepare();

        Artifact artifact = artifactRepository
                .getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Genesis - Together and Apart"))
                .findFirst()
                .orElseThrow();
        artifact.setDuration(Optional.ofNullable(artifact.getDuration()).orElse(0L) + 11);
        artifactRepository.save(artifact);
        log.info("Saved artifact: " + artifact);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(5)).isEqualTo(
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
    @Order(17)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactTrackDurationDifferentShouldFail() {
        this.internalPrepare();

        Track track = trackRepository
                .getTracksByArtifactType(artifactType)
                .stream()
                .filter(t -> t.getTitle().equals("Genesis - Together and Apart"))
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

        assertThat(pi.getProcessDetails().get(10)).isEqualTo(
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
    @Order(18)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileMissingBitrateShouldFail() {
        this.internalPrepare();

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

        assertThat(pi.getProcessDetails().get(6)).isEqualTo(
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
    @Order(19)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileMissingWidthShouldFail() {
        this.internalPrepare();

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

        assertThat(pi.getProcessDetails().get(7)).isEqualTo(
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
    @Order(20)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileMissingHeightShouldFail() {
        this.internalPrepare();

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

        assertThat(pi.getProcessDetails().get(7)).isEqualTo(
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
