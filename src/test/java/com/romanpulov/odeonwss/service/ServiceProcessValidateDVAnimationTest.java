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
import java.util.*;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateDVAnimationTest {
    private final static List<String> DV_PRODUCT_NAMES = List.of(
            "Plunder and Lightning",
            "Louie’s Last Stand",
            "From Here to Machinery",
            "Kak kazaki kulesh varili",
            "Kak kazaki v futbol igrali",
            "Остров сокровищ",
            "Puss Gets the Boot",
            "The Midnight Snack",
            "Who Killed Who?"
    );

    private static final Logger log = Logger.getLogger(ServiceProcessValidateDVAnimationTest.class.getSimpleName());

    @Value("${test.data.path}")
    String testDataPath;

    private enum TestFolder {
        TF_SERVICE_PROCESS_VALIDATE_DV_ANIMATION_TEST_OK,
        TF_SERVICE_PROCESS_VALIDATE_DV_ANIMATION_TEST_WITH_FOLDERS
    }

    private Map<TestFolder, Path> tempFolders;

    @BeforeAll
    public void setup() throws Exception {
        this.tempFolders = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_ANIMATION_TEST_OK),
                this.testDataPath,
                """
{
  "Talespin": {
    "01 Plunder and Lightning (part-1).avi": "sample_AVI_480_750kB.avi",
    "01 Plunder and Lightning (part-2).avi": "sample_AVI_480_750kB.avi",
    "02 From Here to Machinery original.avi": "sample_AVI_480_750kB.avi",
    "03 Louie’s Last Stand.avi": "sample_AVI_480_750kB.avi"
  },
  "Казаки": {
    "01 Kak kazaki kulesh varili.m4v": "sample_960x540.m4v",
    "02 Kak kazaki v futbol igrali.mkv": "sample_1280x720_600.mkv"
  },
  "Остров сокровищ": {
    "Остров Сокровищ (1988) (1).mp4": "sample_MP4_480_1_5MG.mp4",
    "Остров Сокровищ (1988) (2).mp4": "sample_MP4_640_3MG.mp4"
  },
  "Том и Джерри": {
    "001 Puss Gets the Boot (1940).avi": "sample_AVI_480_750kB.avi",
    "002 The Midnight Snack (1941).avi": "sample_AVI_480_750kB.avi",
    "003 Who Killed Who.avi": "sample_AVI_480_750kB.avi"
  }
}
                     """
        );

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_ANIMATION_TEST_WITH_FOLDERS),
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
        FileTreeGenerator.deleteTempFiles(tempFolders.values());
    }

    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_ANIMATION_VALIDATOR;

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
        this.artifactType = artifactTypeRepository.getWithDVAnimation();
    }

    private void internalPrepare() {
        // prepare products
        dataGenerator.createProductsFromList(artifactType, DV_PRODUCT_NAMES);

        service.executeProcessor(
                ProcessorType.DV_ANIMATION_LOADER,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_ANIMATION_TEST_OK).toString());

        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        log.info("Animation loader Processing info: " + service.getProcessInfo());
        /*
        var dvProductList = Arrays.stream(DV_PRODUCT_NAMES).map(s -> {
            DVProduct dvProduct = new DVProduct();
            dvProduct.setArtifactType(artifactType);
            dvProduct.setDvOrigin(dvOriginRepository.findById(1L).orElseGet(() -> {
                DVOrigin dvOrigin = new DVOrigin();
                dvOrigin.setName("New Origin");
                dvOriginRepository.save(dvOrigin);

                return dvOrigin;
            }));
            dvProduct.setTitle(s);
            dvProduct.setOriginalTitle(s + " original");

            return dvProduct;
        }).collect(Collectors.toList());

        dvProductRepository.saveAll(dvProductList);

        service.executeProcessor(ProcessorType.DV_ANIMATION_LOADER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        log.info("Animation loader Processing info: " + service.getProcessInfo());

         */
    }

    private void executeProcessorOk() {
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_ANIMATION_TEST_OK).toString());
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
                        ProcessDetailInfo.fromMessage("Started Animation Validator"),
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
    @Order(3)
    void testContainsFoldersShouldFail() {
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_ANIMATION_TEST_WITH_FOLDERS).toString());
        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(0)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Animation Validator"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(pi.getProcessDetails().get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage(
                                "Expected file, found: " + Path.of(
                                        tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_DV_ANIMATION_TEST_WITH_FOLDERS).toString(),
                                        "Aerosmith",
                                        "2004 Honkin'On Bobo"
                                )),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifacts not in files",
                                List.of("Talespin", "Казаки", "Остров сокровищ", "Том и Джерри")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(pi.getProcessDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.FAILURE,
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
        Artifact artifact = artifactRepository.findAll().get(0);
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
                .filter(a -> a.getTitle().equals("Казаки"))
                .findFirst().orElseThrow();
        Track track = trackRepository
                .findByIdWithMediaFiles(artifact.getTracks().get(0).getId())
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
                        .filter(c -> c.getTitle().equals("Plunder and Lightning"))
                        .findFirst()
                        .orElseThrow()
                        .getId()
                ).orElseThrow();
        track.getMediaFiles().removeIf(m -> m.getName().equals("01 Plunder and Lightning (part-1).avi"));
        trackRepository.save(track);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in database",
                                List.of("Talespin >> 01 Plunder and Lightning (part-1).avi")),
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
                .filter(a -> a.getTitle().equals("Казаки"))
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
                .filter(m -> m.getName().equals("01 Plunder and Lightning (part-2).avi"))
                .findFirst()
                .orElseThrow();
        mediaFile.setArtifact(null);
        mediaFileRepository.save(mediaFile);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact media files not in database",
                                List.of(mediaFile.getName())),
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
                .filter(v -> v.getTitle().equals("Talespin"))
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
                                List.of("Talespin")),
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
                .filter(a -> a.getTitle().equals("Talespin"))
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
                                List.of("Talespin")),
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
                .filter(t -> t.getTitle().equals("Plunder and Lightning"))
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
                                List.of("Talespin >> Plunder and Lightning")),
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
                .filter(t -> t.getTitle().equals("From Here to Machinery original"))
                .findFirst()
                .orElseThrow();
        assertThat(track1.getNum()).isEqualTo(2L);

        track1.setNum(3L);
        trackRepository.save(track1);

        executeProcessorOk();

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(9)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Track numbers for artifact not increasing monotonically",
                                List.of("Talespin")),
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
                .filter(a -> a.getTitle().equals("Talespin"))
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
                .filter(a -> a.getTitle().equals("Talespin"))
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
                .filter(t -> t.getTitle().equals("Puss Gets the Boot"))
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
