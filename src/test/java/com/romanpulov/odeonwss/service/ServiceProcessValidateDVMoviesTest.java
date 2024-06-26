package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.MediaFileValidator;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateDVMoviesTest {
    private final static String[] DV_PRODUCT_NAMES = {
            "Крепкий орешек",
            "Лицензия на убийство",
            "Обыкновенное чудо",
            "Рецепт убийства",
            "Убийство по книге"
    };

    private static final Logger log = Logger.getLogger(ServiceProcessValidateDVMoviesTest.class.getSimpleName());
    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MOVIES_VALIDATOR;

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
        this.artifactType = artifactTypeRepository.getWithDVMovies();
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

            return dvProduct;
        }).collect(Collectors.toList());

        dvProductRepository.saveAll(dvProductList);

        service.executeProcessor(ProcessorType.DV_MOVIES_LOADER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        log.info("Movies Importer Processing info: " + service.getProcessInfo());
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
                        ProcessDetailInfo.fromMessage("Started Movies Validator"),
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
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/ok/MP3 Music/");
        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(0)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Movies Validator"),
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
                                List.of("Коломбо", "Крепкий орешек", "Лицензия на убийство", "Обыкновенное чудо")),
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
                .filter(a -> a.getTitle().equals("Крепкий орешек"))
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
                        .filter(c -> c.getTitle().equals("Обыкновенное чудо"))
                        .findFirst()
                        .orElseThrow()
                        .getId()
                ).orElseThrow();
        track.getMediaFiles().removeIf(m -> m.getName().equals("Part 2.avi"));
        trackRepository.save(track);

        service.executeProcessor(PROCESSOR_TYPE);

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in database",
                                List.of("Обыкновенное чудо >> Part 2.avi")),
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
                .filter(a -> a.getTitle().equals("Крепкий орешек"))
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
                .filter(m -> m.getName().equals("Part 2.avi"))
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
    void testArtifactWithoutTracksShouldFail() {
        this.internalPrepare();
        var artifact = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .filter(v -> v.getTitle().equals("Крепкий орешек"))
                .findFirst()
                .orElseThrow();
        assertThat(artifact).isNotNull();
        var tracks = trackRepository.findAllByArtifact(artifact);
        trackRepository.deleteAll(tracks);

        service.executeProcessor(PROCESSOR_TYPE);
        ProcessInfo pi = service.getProcessInfo();
        var pd = pi.getProcessDetails();
        assertThat(pd.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "No tracks for artifact",
                                List.of("Крепкий орешек")),
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
                .filter(a -> a.getTitle().equals("Крепкий орешек"))
                .findFirst().orElseThrow();
        assertThat(artifact).isNotNull();

        var mediaFiles = mediaFileRepository.findAllByArtifactId(artifact.getId());
        assertThat(mediaFiles.isEmpty()).isFalse();
        mediaFileRepository.deleteAll(mediaFiles);

        service.executeProcessor(PROCESSOR_TYPE);
        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        var pd = pi.getProcessDetails();
        assertThat(pd.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "No media files for artifact",
                                List.of("Крепкий орешек")),
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
                .filter(t -> t.getTitle().equals("Убийство по книге"))
                .findFirst().orElseThrow();

        track_to_remove.setDvProducts(Set.of());
        trackRepository.save(track_to_remove);

        service.executeProcessor(PROCESSOR_TYPE);

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(11)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Tracks without product",
                                List.of("Коломбо >> Убийство по книге")),
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
                .filter(t -> t.getTitle().equals("Рецепт убийства"))
                .findFirst()
                .orElseThrow();
        assertThat(track1.getNum()).isEqualTo(1L);

        track1.setNum(3L);
        trackRepository.save(track1);

        service.executeProcessor(PROCESSOR_TYPE);

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(9)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Track numbers for artifact not increasing monotonically",
                                List.of("Коломбо")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

    }

    @Test
    @Order(14)
    @Sql({"/schema.sql", "/data.sql"})
    void testHasZeroBitrateShouldFail() {
        this.internalPrepare();

        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("Part 2.avi"))
                .findFirst()
                .orElseThrow();
        mediaFile.setBitrate(0L);
        mediaFileRepository.save(mediaFile);

        service.executeProcessor(PROCESSOR_TYPE);

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(6)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files with empty bitrate",
                                List.of("Обыкновенное чудо >> Part 2.avi")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(15)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileSizeDifferentShouldFail() {
        this.internalPrepare();

        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("Part 1.avi"))
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
                                List.of("Обыкновенное чудо >> Part 1.avi")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(16)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactMediaFileSizeDifferentShouldFail() {
        this.internalPrepare();

        Artifact artifact = artifactRepository
                .getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Крепкий орешек"))
                .findFirst()
                .orElseThrow();
        artifact.setSize(Optional.ofNullable(artifact.getSize()).orElse(0L) + 530);
        artifactRepository.save(artifact);
        log.info("Saved artifact: " + artifact);

        service.executeProcessor(PROCESSOR_TYPE);

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
    @Order(17)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactMediaFileDurationDifferentShouldFail() {
        this.internalPrepare();

        Artifact artifact = artifactRepository
                .getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Коломбо"))
                .findFirst()
                .orElseThrow();
        artifact.setDuration(Optional.ofNullable(artifact.getDuration()).orElse(0L) + 11);
        artifactRepository.save(artifact);
        log.info("Saved artifact: " + artifact);

        service.executeProcessor(PROCESSOR_TYPE);

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
    @Order(18)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactTrackDurationDifferentShouldFail() {
        this.internalPrepare();

        Track track = trackRepository
                .getTracksByArtifactType(artifactType)
                .stream()
                .filter(t -> t.getTitle().equals("Рецепт убийства"))
                .findFirst()
                .orElseThrow();
        Artifact artifact = artifactRepository
                .findById(track.getArtifact().getId())
                .orElseThrow();

        track.setDuration(Optional.ofNullable(track.getDuration()).orElse(0L) + 5);
        trackRepository.save(track);
        log.info("Saved track: " + track);

        service.executeProcessor(PROCESSOR_TYPE);

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
    @Order(19)
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

        service.executeProcessor(PROCESSOR_TYPE);

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
    @Order(20)
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

        service.executeProcessor(PROCESSOR_TYPE);

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
    @Order(21)
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

        service.executeProcessor(PROCESSOR_TYPE);

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
