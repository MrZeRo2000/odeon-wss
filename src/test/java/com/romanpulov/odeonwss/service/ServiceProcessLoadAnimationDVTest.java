package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.entity.DVProduct;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.ValueValidator;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessLoadAnimationDVTest {
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

    private static final Logger log = Logger.getLogger(ServiceProcessLoadAnimationDVTest.class.getSimpleName());

    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_ANIMATION_LOADER;
    private ArtifactType artifactType;

    @Autowired
    ProcessService processService;

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

    @Autowired
    DataSource dataSource;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(value = false)
    void testPrepare() {
        this.artifactType = artifactTypeRepository.getWithDVAnimation();

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
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void testSuccess() throws Exception {
        processService.executeProcessor(PROCESSOR_TYPE);
        ProcessInfo pi = processService.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        log.info("Animation Loader Processing info: " + processService.getProcessInfo());

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        assertThat(processDetails.get(1)).isEqualTo(new ProcessDetail(
                ProcessDetailInfo.fromMessage("Artifacts loaded"),
                ProcessingStatus.INFO,
                4,
                null));

        assertThat(processDetails.get(2)).isEqualTo(new ProcessDetail(
                ProcessDetailInfo.fromMessage("Tracks loaded"),
                ProcessingStatus.INFO,
                9,
                null));

        assertThat(processDetails.get(3)).isEqualTo(new ProcessDetail(
                ProcessDetailInfo.fromMessage("Media files loaded"),
                ProcessingStatus.INFO,
                11,
                null));

        trackRepository.getTracksByArtifactType(artifactType).forEach(c -> {
            Track productsTrack = trackRepository.findByIdWithProducts(c.getId()).orElseThrow();
            Assertions.assertEquals(1, productsTrack.getDvProducts().size());
            //every track has a media file
            assertThat(trackRepository.findByIdWithMediaFiles(c.getId()).orElseThrow().getMediaFiles().size()).isGreaterThan(0);
        });

        // validate DTO
        var dto = processService.getById(1L);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getProcessDetails().get(1).getUpdateDateTime()).isNotNull();
        assertThat(dto.getProcessDetails().get(1).getMessage()).isEqualTo("Artifacts loaded");
        assertThat(dto.getProcessDetails().get(1).getStatus()).isEqualTo(ProcessingStatus.INFO);
        assertThat(dto.getProcessDetails().get(1).getRows()).isEqualTo(4L);
        assertThat(dto.getProcessDetails().get(1).getItems().isEmpty()).isTrue();
        assertThat(dto.getProcessDetails().get(1).getProcessingAction()).isNull();
    }

    @Test
    @Order(4)
    @Rollback(value = false)
    void testSuccessRepeated() {
        int oldArtifacts = artifactRepository.getAllByArtifactType(artifactType).size();
        Assertions.assertTrue(oldArtifacts > 0);

        int oldTracks = trackRepository.getTracksByArtifactType(artifactType).size();
        Assertions.assertTrue(oldTracks > 0);
        assertThat(oldArtifacts).isLessThanOrEqualTo(oldTracks);

        int oldMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(artifactType).size();
        Assertions.assertTrue(oldMediaFiles > 0);
        Assertions.assertTrue(oldMediaFiles >= oldTracks);

        processService.executeProcessor(PROCESSOR_TYPE);

        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());
        Assertions.assertEquals(
                new ProcessDetail(ProcessDetailInfo.fromMessage("Artifacts loaded"), ProcessingStatus.INFO, 0, null),
                processService.getProcessInfo().getProcessDetails().get(1)
        );
        Assertions.assertEquals(
                new ProcessDetail(ProcessDetailInfo.fromMessage("Tracks loaded"), ProcessingStatus.INFO, 0, null),
                processService.getProcessInfo().getProcessDetails().get(2)
        );
        Assertions.assertEquals(
                new ProcessDetail(ProcessDetailInfo.fromMessage("Media files loaded"), ProcessingStatus.INFO, 0, null),
                processService.getProcessInfo().getProcessDetails().get(3)
        );

        trackRepository.getTracksByArtifactType(artifactType).forEach(c -> {
            Track track = trackRepository.findByIdWithProducts(c.getId()).orElseThrow();
            Assertions.assertEquals(1, track.getDvProducts().size());
        });

        int newArtifacts = artifactRepository.getAllByArtifactType(artifactType).size();
        Assertions.assertEquals(oldArtifacts, newArtifacts);

        int newTracks = trackRepository.getTracksByArtifactType(artifactType).size();
        Assertions.assertEquals(oldTracks, newTracks);

        int newMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(artifactType).size();
        Assertions.assertEquals(oldMediaFiles, newMediaFiles);
    }

    @Test
    @Order(5)
    @Rollback(value = false)
    void testSizeDuration() {
        artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .forEach(artifact -> {
                    Assertions.assertFalse(ValueValidator.isEmpty(artifact.getSize()));
                    Assertions.assertFalse(ValueValidator.isEmpty(artifact.getDuration()));

                    assertThat(artifact.getDuration()).isEqualTo(
                            artifact.getTracks()
                                    .stream()
                                    .map(Track::getDuration)
                                    .reduce(0L, (a, b) -> b != null ? Long.sum(a, b) : 0)
                                    .longValue()
                    );
                });
        trackRepository.getTracksByArtifactType(artifactType)
                .forEach(track ->
                    Assertions.assertFalse(ValueValidator.isEmpty(track.getDuration()))
                );
    }

    @Test
    @Order(5)
    @Rollback(value = false)
    void testTrackNumber() {
        var artifact = artifactRepository
                .getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Talespin"))
                .findFirst()
                .orElseThrow();

        var track = trackRepository.findTrackByArtifactAndTitle(artifact, "From Here to Machinery original").orElseThrow();
        assertThat(track.getNum()).isEqualTo(2L);

        track = trackRepository.findTrackByArtifactAndTitle(artifact, "Plunder and Lightning").orElseThrow();
        assertThat(track.getNum()).isEqualTo(1L);
    }
}
