package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateDVAnimationTest {
    private final static String[] DV_PRODUCT_NAMES = {
            "Plunder and Lightning",
            "Louie’s Last Stand",
            "From Here to Machinery",
            "Kak kazaki kulesh varili",
            "Kak kazaki v futbol igrali",
            "Остров сокровищ",
            "Puss Gets the Boot",
            "The Midnight Snack",
            "Who Killed Who?"
    };

    private static final Logger log = Logger.getLogger(ServiceProcessValidateDVAnimationTest.class.getSimpleName());
    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_ANIMATION_VALIDATOR;

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

    @Autowired
    private DVOriginRepository dvOriginRepository;

    @Autowired
    private DVProductRepository dvProductRepository;

    private ArtifactType artifactType;

    @BeforeEach
    void beforeEach() {
        this.artifactType = artifactTypeRepository.getWithDVAnimation();
    }

    private void internalPrepare() {
        // prepare products
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
        service.executeProcessor(PROCESSOR_TYPE);
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
                        ProcessDetailInfo.fromMessage("Products for tracks validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Monotonically increasing track numbers validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );


        assertThat(pi.getProcessDetails().get(6)).isEqualTo(
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
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/ok/MP3 Music/");
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
                                "Expected file, found: ..\\odeon-test-data\\ok\\MP3 Music\\Aerosmith\\2004 Honkin'On Bobo"),
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
        service.executeProcessor(PROCESSOR_TYPE);

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

        service.executeProcessor(PROCESSOR_TYPE);

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

        service.executeProcessor(PROCESSOR_TYPE);

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

        service.executeProcessor(PROCESSOR_TYPE);

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

        service.executeProcessor(PROCESSOR_TYPE);

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

        service.executeProcessor(PROCESSOR_TYPE);

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
    void testMissingProductForTrackShouldFail() {
        this.internalPrepare();

        //remove a product
        var track_to_remove = trackRepository.getTracksByArtifactType(artifactType)
                .stream()
                .filter(t -> t.getTitle().equals(DV_PRODUCT_NAMES[0]))
                .findFirst().orElseThrow();

        track_to_remove.setDvProducts(Set.of());
        trackRepository.save(track_to_remove);

        service.executeProcessor(PROCESSOR_TYPE);

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Tracks without product",
                                List.of(DV_PRODUCT_NAMES[0])),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }


    @Test
    @Order(11)
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

        service.executeProcessor(PROCESSOR_TYPE);

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Track numbers for artifact not increasing monotonically",
                                List.of("Talespin")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

    }
}
