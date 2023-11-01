package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.config.DatabaseConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(value = "test-07")
public class ServiceProcessValidateMP3Test {
    public static final ProcessorType PROCESSOR_TYPE = ProcessorType.MP3_VALIDATOR;
    private ArtifactType artifactType;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    DatabaseConfiguration databaseConfiguration;

    ProcessService service;

    List<String> artistNames;

    @Autowired
    public ServiceProcessValidateMP3Test(ProcessService service) {
        this.service = service;
    }

    private void prepareInternal() {
        if (this.artifactType == null) {
            this.artifactType = artifactTypeRepository.getWithMP3();
        }

        DbManagerService.loadOrPrepare(databaseConfiguration, DbManagerService.DbType.DB_LOADED_MP3, () -> {
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
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        List<String> artistNames = artistRepository
                .getAllByType(ArtistType.ARTIST)
                .stream()
                .map(Artist::getName)
                .toList();

        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started MP3 Validator"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artists not in database or have no artifacts and tracks",
                                artistNames.stream().sorted().collect(Collectors.toList())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
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
        assertThat(artistRepository.getAllByType(ArtistType.ARTIST).size()).isEqualTo(3);
    }

    @Test
    @Order(3)
    void testOk() {
        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started MP3 Validator"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Tracks validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files size validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files duration validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files size mismatch validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Monotonically increasing track numbers validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact tracks duration validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
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
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Error parsing artifact name",
                                List.of("Aerosmith >> 2004  Honkin'On Bobo")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(processDetails.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artists not in files or have no artifacts and tracks",
                                List.of("Kosheen", "Various Artists")),
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
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artists not in files or have no artifacts and tracks",
                                List.of("Kosheen", "Various Artists")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(6)
    void testAdditionalFileArtist() {
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/validation_mp3_additional_artist/");
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artists not in database or have no artifacts and tracks",
                                List.of("Accept")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(7)
    void testMissingFileArtifact() {
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/validation_mp3_missing_artifact/");
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifacts not in files", List.of("Kosheen >> 2007 Damage")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(8)
    void testMissingFileTracks() {
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/validation_mp3_missing_tracks/");
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        int id = 1;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Tracks not in files",
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
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started MP3 Validator"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Tracks validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files size validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact media files duration validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files size mismatch validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Monotonically increasing track numbers validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifact tracks duration validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );
    }

    @Test
    @Order(12)
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql"})
    void containsFilesShouldFail() {
        this.prepareInternal();
        service.executeProcessor(PROCESSOR_TYPE, "../odeon-test-data/ok/MP3 Music/Kosheen/2004 Kokopelli");
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(processDetails.get(1).getInfo().getMessage()).contains("Expected directory, found");
        assertThat(processDetails.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artists not in files or have no artifacts and tracks",
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
                .withArtifactType(artifactType)
                .withArtist(artist)
                .withTitle("New Artifact")
                .withYear(2000L)
                .build();
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        int id = 1;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifacts not in files",
                                List.of(artist.getName() + " >> 2000 New Artifact")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
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
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        int id = 1;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists validated"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifacts not in database",
                                List.of("Kosheen >> 2004 Kokopelli")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(16)
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql"})
    void testNewFileInDbShouldFail() {
        this.prepareInternal();
        Artifact artifact = artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Kokopelli") && !Objects.isNull(a.getYear()) && a.getYear().equals(2004L))
                .findFirst().orElseThrow();
        Track track = trackRepository
                .findByIdWithMediaFiles(artifact.getTracks().get(0).getId())
                .orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("13 - New Track.mp3");
        mediaFile.setFormat("MP3");
        mediaFile.setSize(84583733L);
        mediaFile.setBitrate(320L);
        mediaFileRepository.save(mediaFile);

        track.getMediaFiles().add(mediaFile);
        trackRepository.save(track);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in files",
                                List.of("Kosheen >> 2004 Kokopelli >> " + mediaFile.getName())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }


    @Test
    @Order(17)
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql"})
    void testNewFileInFilesShouldFail() {
        this.prepareInternal();
        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("07 - Swamp.mp3"))
                .findFirst()
                .orElseThrow();
        mediaFileRepository.delete(mediaFile);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in database",
                                List.of("Kosheen >> 2004 Kokopelli >> " + mediaFile.getName())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(processDetails.get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact media files not in database",
                                List.of("Kosheen >> 2004 Kokopelli >> " + mediaFile.getName())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(18)
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql"})
    void testNewArtifactFileInDbShouldFail() {
        this.prepareInternal();
        Artifact artifact = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Damage"))
                .findFirst().orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("17 - My Life.mp3");
        mediaFile.setFormat("MP3");
        mediaFile.setSize(630453L);
        mediaFile.setBitrate(256L);
        mediaFile.setArtifact(artifact);
        mediaFileRepository.save(mediaFile);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact media files not in files",
                                List.of("Kosheen >> 2007 Damage >> 17 - My Life.mp3")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(19)
    @Sql({"/schema.sql", "/data.sql", "/main_artists.sql"})
    void testNewArtifactFileInFilesShouldFail() {
        this.prepareInternal();
        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("01 - Damage.mp3"))
                .findFirst()
                .orElseThrow();
        mediaFile.setArtifact(null);
        mediaFileRepository.save(mediaFile);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact media files not in database",
                                List.of("Kosheen >> 2007 Damage >> 01 - Damage.mp3")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(20)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileSizeDifferentShouldFail() {
        this.prepareInternal();

        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("01 - Wasting My Time.mp3"))
                .findFirst()
                .orElseThrow();
        mediaFile.setSize(mediaFile.getSize() + 100L);
        mediaFileRepository.save(mediaFile);

        Artifact artifact = mediaFile.getArtifact();
        artifact.setSize(Optional.ofNullable(artifact.getSize()).orElse(0L) + 100L);
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        Assertions.assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        Assertions.assertThat(pi.getProcessDetails().get(8)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files size mismatch",
                                List.of("Kosheen >> 2004 Kokopelli >> 01 - Wasting My Time.mp3")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(21)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactMediaFileSizeDifferentShouldFail() {
        this.prepareInternal();

        Artifact artifact = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Damage"))
                .findFirst().orElseThrow();

        artifact.setSize(Optional.ofNullable(artifact.getSize()).orElse(0L) + 530);
        artifactRepository.save(artifact);

        service.executeProcessor(PROCESSOR_TYPE);

        ProcessInfo pi = service.getProcessInfo();
        Assertions.assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        Assertions.assertThat(pi.getProcessDetails().get(6)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact size does not match media files size",
                                List.of("Kosheen >> 2007 Damage")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(22)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactMediaFileDurationDifferentShouldFail() {
        this.prepareInternal();

        Artifact artifact = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Damage"))
                .findFirst().orElseThrow();

        artifact.setDuration(Optional.ofNullable(artifact.getDuration()).orElse(0L) + 540);
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(7)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact duration does not match media files duration",
                                List.of("Kosheen >> 2007 Damage")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(23)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactTrackDurationDifferentShouldFail() {
        this.prepareInternal();

        Track track = trackRepository
                .getTracksByArtifactType(artifactType)
                .stream()
                .filter(t -> t.getTitle().equals("Thief"))
                .findFirst()
                .orElseThrow();
        Artifact artifact = artifactRepository
                .findById(track.getArtifact().getId())
                .orElseThrow();

        track.setDuration(Optional.ofNullable(track.getDuration()).orElse(0L) + 5);
        trackRepository.save(track);

        service.executeProcessor(PROCESSOR_TYPE);

        ProcessInfo pi = service.getProcessInfo();
        Assertions.assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        Assertions.assertThat(pi.getProcessDetails().get(10)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact duration does not match tracks duration",
                                List.of("Kosheen >> 2007 Damage")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }
}
