package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.entity.DVProduct;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.ValueValidator;
import com.romanpulov.odeonwss.service.processor.model.ProcessDetail;
import com.romanpulov.odeonwss.service.processor.model.ProcessDetailInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessLoadMoviesDVTest {
    private final static String[] DV_PRODUCT_NAMES = {
            "Крепкий орешек",
            "Лицензия на убийство",
            "Обыкновенное чудо",
            "Рецепт убийства",
            "Убийство по книге"
    };

    private static final Logger log = Logger.getLogger(ServiceProcessLoadMoviesDVTest.class.getSimpleName());

    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MOVIES_LOADER;
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
        this.artifactType = artifactTypeRepository.getWithDVMovies();

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
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void testContainsFilesShouldFail() {
        Assertions.assertEquals(0, artifactRepository.getAllByArtifactType(artifactType).size());

        processService.executeProcessor(PROCESSOR_TYPE, "");
        log.info("Movies Loader Processing info: " + processService.getProcessInfo());

        Assertions.assertEquals(ProcessingStatus.FAILURE, processService.getProcessInfo().getProcessingStatus());
        Assertions.assertEquals(0, artifactRepository.getAllByArtifactType(artifactType).size());
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void testSuccess() {
        processService.executeProcessor(PROCESSOR_TYPE);
        log.info("Movies Loader Processing info: " + processService.getProcessInfo());

        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());
        Assertions.assertEquals(
                new ProcessDetail(ProcessDetailInfo.fromMessage("Artifacts loaded"), ProcessingStatus.INFO, 4, null),
                processService.getProcessInfo().getProcessDetails().get(1)
        );
        Assertions.assertEquals(
                new ProcessDetail(ProcessDetailInfo.fromMessage("Tracks loaded"), ProcessingStatus.INFO, 5, null),
                processService.getProcessInfo().getProcessDetails().get(2)
        );
        Assertions.assertEquals(
                new ProcessDetail(ProcessDetailInfo.fromMessage("Media files loaded"), ProcessingStatus.INFO, 6, null),
                processService.getProcessInfo().getProcessDetails().get(3)
        );

        trackRepository.getTracksByArtifactType(artifactType).forEach(c -> {
            Track productsTrack = trackRepository.findByIdWithProducts(c.getId()).orElseThrow();
            Assertions.assertEquals(1, productsTrack.getDvProducts().size());
        });
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
                .filter(a -> a.getTitle().equals("Коломбо"))
                .findFirst()
                .orElseThrow();

        var track = trackRepository.findTrackByArtifactAndTitle(artifact, "Убийство по книге").orElseThrow();
        assertThat(track.getNum()).isEqualTo(2L);

        track = trackRepository.findTrackByArtifactAndTitle(artifact, "Рецепт убийства").orElseThrow();
        assertThat(track.getNum()).isEqualTo(1L);
    }

    @Test
    @Order(10)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(value = false)
    void testLoadWithoutOneProduct() {
        var dvProductList = Arrays.stream(DV_PRODUCT_NAMES).filter(s -> !s.equals(DV_PRODUCT_NAMES[0])).map(s -> {
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

        processService.executeProcessor(PROCESSOR_TYPE);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());

        var resultTracks = trackRepository.getTracksByArtifactType(artifactType);
        assertThat(resultTracks.size()).isEqualTo(5);

        int tracksWithProducts = resultTracks.stream().map(track ->
            trackRepository.findByIdWithProducts(track.getId()).orElseThrow().getDvProducts().size()
        ).mapToInt(Integer::intValue).sum();
        assertThat(tracksWithProducts).isEqualTo(4);
    }
}
