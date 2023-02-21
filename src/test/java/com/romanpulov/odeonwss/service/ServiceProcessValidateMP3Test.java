package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import com.romanpulov.odeonwss.service.processor.model.ProgressDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateMP3Test {
    public static final ProcessorType PROCESSOR_TYPE = ProcessorType.MP3_VALIDATOR;
    private static final ArtifactType ARTIFACT_TYPE = ArtifactType.withMP3();

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    AppConfiguration appConfiguration;

    ProcessService service;

    List<String> artistNames;

    @Autowired
    public ServiceProcessValidateMP3Test(ProcessService service) {
        this.service = service;
    }

    private void prepareInternal() {
        DbManagerService.loadOrPrepare(appConfiguration, DbManagerService.DbType.DB_LOADED_MP3, () -> {
            service.executeProcessor(ProcessorType.MP3_LOADER, null);
            assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        });
        if (this.artistNames == null) {
            this.artistNames = artistRepository
                    .getAllByType(ArtistType.ARTIST)
                    .stream()
                    .map(Artist::getName)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    private ProcessInfo executeProcessor() {
        service.executeProcessor(PROCESSOR_TYPE);
        return service.getProcessInfo();
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql"})
    void testLoadEmptyShouldFail() {
        ProcessInfo pi = executeProcessor();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        List<String> artistNames = artistRepository
                .getAllByType(ArtistType.ARTIST)
                .stream()
                .map(Artist::getName)
                .collect(Collectors.toList());

        int id = 0;
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Started MP3 Validator", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artists not in database or have no artifacts and compositions",
                                artistNames.stream().sorted().collect(Collectors.toList())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(progressDetails.get(id)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

    }

    @Test
    @Order(2)
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql"})
    void testLoad() {
        this.prepareInternal();
        assertThat(artistRepository.getAllByType(ArtistType.ARTIST).size()).isEqualTo(2);
    }

    @Test
    @Order(3)
    void testOk() {
        ProcessInfo pi = executeProcessor();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        int id = 0;
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Started MP3 Validator", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists validated", List.of()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifacts validated", List.of()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Compositions validated", List.of()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Media files validated", List.of()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifact media files validated", List.of()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(progressDetails.get(id)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );
    }

    @Test
    @Order(4)
    void testWrongTitleMissingFileArtist() {
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/wrong_artifact_title/");
        ProcessInfo pi = service.getProcessInfo();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(progressDetails.get(1)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Error parsing artifact name",
                                List.of("Aerosmith >> 2004  Honkin'On Bobo")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(progressDetails.get(2)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artists not in files or have no artifacts and compositions",
                                List.of("Kosheen")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(5)
    void testMissingFileArtist() {
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/validation_mp3_missing_artist/");
        ProcessInfo pi = service.getProcessInfo();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(progressDetails.get(1)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artists not in files or have no artifacts and compositions",
                                List.of("Kosheen")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(6)
    void testAdditionalFileArtist() throws Exception {
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/validation_mp3_additional_artist/");
        ProcessInfo pi = service.getProcessInfo();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(progressDetails.get(1)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artists not in database or have no artifacts and compositions",
                                List.of("Accept")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(7)
    void testMissingFileArtifact() throws Exception {
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/validation_mp3_missing_artifact/");
        ProcessInfo pi = service.getProcessInfo();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(progressDetails.get(1)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists validated", List.of()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(progressDetails.get(2)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artifacts not in files", List.of("Kosheen >> 2007 Damage")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(8)
    void testMissingFileCompositions() throws Exception {
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/validation_mp3_missing_compositions/");
        ProcessInfo pi = service.getProcessInfo();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        int id = 1;
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists validated", List.of()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifacts validated", List.of()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(progressDetails.get(id)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Compositions not in files",
                                List.of(
                                        "Kosheen >> 2004 Kokopelli >> 02 - All In My Head (Radio Edit)",
                                        "Kosheen >> 2004 Kokopelli >> 03 - Crawling",
                                        "Kosheen >> 2004 Kokopelli >> 04 - Avalanche"
                                )),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(11)
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql"})
    void validateOk() {
        this.prepareInternal();
        ProcessInfo pi = executeProcessor();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        int id = 0;
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Started MP3 Validator", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifacts validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Compositions validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Media files validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artifact media files validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
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
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/ok/MP3 Music/Kosheen/2004 Kokopelli");
        ProcessInfo pi = service.getProcessInfo();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();

        assertThat(progressDetails.get(1).getInfo().getMessage()).contains("Expected directory, found");
        assertThat(progressDetails.get(2)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artists not in files or have no artifacts and compositions",
                                artistNames),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(14)
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql"})
    void testNewArtifactInDbShouldFail() {
        this.prepareInternal();

        Artist artist = artistRepository.findAll().iterator().next();
        assertThat(artist).isNotNull();

        Artifact artifact = (new EntityArtifactBuilder())
                .withArtifactType(ARTIFACT_TYPE)
                .withArtist(artist)
                .withTitle("New Artifact")
                .withYear(2000L)
                .build();
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        int id = 1;
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artifacts not in files",
                                List.of(artist.getName() + " >> 2000 New Artifact")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(15)
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql"})
    void testNewArtifactInFilesShouldFail() {
        this.prepareInternal();
        Artifact artifact = artifactRepository
                .findAll()
                .stream()
                .filter(a -> a.getTitle().equals("Kokopelli"))
                .findFirst()
                .orElseThrow();
        artifactRepository.delete(artifact);

        ProcessInfo pi = executeProcessor();
        List<ProgressDetail> progressDetails = pi.getProgressDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        int id = 1;
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Artists validated", new ArrayList<>()),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id++)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo(
                                "Artifacts not in database",
                                List.of("Kosheen >> 2004 Kokopelli")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(progressDetails.get(id)).isEqualTo(
                new ProgressDetail(
                        new ProgressDetail.ProgressInfo("Task status", new ArrayList<>()),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

}
