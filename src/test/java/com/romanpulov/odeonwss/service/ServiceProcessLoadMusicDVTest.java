package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.generator.DataGenerator;
import com.romanpulov.odeonwss.generator.FileTreeGenerator;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.ValueValidator;
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
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@ActiveProfiles(value = "test-02")
public class ServiceProcessLoadMusicDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadMusicDVTest.class.getSimpleName());

    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.DV_MUSIC_LOADER;
    private ArtifactType artifactType;

    @Value("${test.data.path}")
    String testDataPath;

    @Autowired
    DataGenerator dataGenerator;

    @Autowired
    ProcessService processService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private TrackRepository trackRepository;

    private ProcessInfo executeProcessor(TestFolder testFolder) {
        processService.executeProcessor(PROCESSOR_TYPE, tempDirs.get(testFolder).toString());
        return processService.getProcessInfo();
    }

    private Map<TestFolder, Path> tempDirs;

    private enum TestFolder {
        TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_OK,
        TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITHOUT_PARCELABLE,
        TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_TITLES_NO_ARTIST,
        TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_ARTISTS_AND_TITLE,
        TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_ARTIFACT_ARTIST_VARIABLE_LENGTH,
        TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_ARTIFACT_INVALID_PATH_CHARACTER
    }

    @BeforeAll
    public void setup() throws Exception {
        log.info("Before all");

        tempDirs = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_OK),
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
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITHOUT_PARCELABLE),
                this.testDataPath,
                """
{
    "Tori Amos - Fade to Red 2006": {
        "Tori Amos - Fade to Red Disk 1 2006.mkv": "sample_1280x720_600.mkv",
        "Tori Amos - Fade to Red Disk 2 2006.mkv": "sample_1280x720_600.mkv"
    }
}
                """
        );

        FileTreeGenerator.generateFromJSON(
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_TITLES_NO_ARTIST),
                this.testDataPath,
                """
{
    "The Cure - In Orange": {
        "01 Shake dog shake.mkv": "sample_1280x720_600.mkv",
        "02 Never Enough.mkv": "sample_1280x720_600.mkv"
    }
}
                """
        );

        FileTreeGenerator.generateFromJSON(
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_ARTISTS_AND_TITLE),
                this.testDataPath,
                """
{
    "Beautiful Voices": {
        "01 Nightwish - Ghost Love Score.mkv": "sample_1280x720_600.mkv",
        "02 Mandragora Scream - Vision They Shared.mkv": "sample_1280x720_600.mkv",
        "03 Tapping The Vein - Butterfly (Unsensored)(2000).mkv": "sample_1280x720_600.mkv"
    }
}
                """
        );

        FileTreeGenerator.generateFromJSON(
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_ARTIFACT_ARTIST_VARIABLE_LENGTH),
                this.testDataPath,
                """
{
    "Black Sabbath - Videos": {
        "01 Paranoid.mkv": "sample_1280x720_600.mkv",
        "02 Iron Man.mkv": "sample_1280x720_600.mkv"
    }
}
                """
        );

        FileTreeGenerator.generateFromJSON(
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_ARTIFACT_INVALID_PATH_CHARACTER),
                this.testDataPath,
                """
{
    "Therapy - Videos": {
        "01 Isolation.mkv": "sample_1280x720_600.mkv",
        "02 Happy People Have No Stories.mkv": "sample_1280x720_600.mkv"
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

    private void prepareArtists() {
        dataGenerator.createArtistsFromList(
                List.of(
                        "The Cure",
                        "Tori Amos",
                        "Various Artists",
                        "Nightwish",
                        "Mandragora Scream",
                        "Black",
                        "Black Sabbath",
                        "Therapy"));
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(value = false)
    void testPrepare() {
        this.artifactType = artifactTypeRepository.getWithDVMusic();
        prepareArtists();
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void testContainsFilesShouldFail() {
        Assertions.assertEquals(0, artifactRepository.getAllByArtifactType(artifactType).size());

        processService.executeProcessor(PROCESSOR_TYPE, "");
        log.info("Music Loader Processing info: " + processService.getProcessInfo());

        assertThat(processService.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(artifactRepository.getAllByArtifactType(artifactType).size()).isEqualTo(0);
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void testSuccess() {
        ProcessInfo pi = executeProcessor(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_OK);
        log.info("Music Loader Processing info: " + pi);
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Video music Loader"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts loaded"),
                        ProcessingStatus.INFO,
                        3,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files loaded"),
                        ProcessingStatus.INFO,
                        4,
                        null)
        );

        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );

        var artifacts = artifactRepository.getAllByArtifactType(this.artifactType);
        assertThat(artifacts.get(0).getTitle()).isEqualTo("Beautiful Voices 1");
        assertThat(artifacts.get(0).getYear()).isNull();
        assertThat(artifacts.get(0).getArtist()).isNull();

        assertThat(artifacts.get(1).getTitle()).isEqualTo("The Cure - Picture Show 1991");
        assertThat(artifacts.get(1).getYear()).isEqualTo(1991L);
        assertThat(artifacts.get(1).getArtist()).isNotNull();
        assertThat(Optional.ofNullable(artifacts.get(1).getArtist()).orElseThrow().getId())
                .isEqualTo(artistRepository.findFirstByName("The Cure").orElseThrow().getId());

        assertThat(artifacts.get(2).getTitle()).isEqualTo("Tori Amos - Fade to Red 2006");
        assertThat(artifacts.get(2).getYear()).isEqualTo(2006L);
        assertThat(artifacts.get(2).getArtist()).isNotNull();
        assertThat(Optional.ofNullable(artifacts.get(2).getArtist()).orElseThrow().getId())
                .isEqualTo(artistRepository.findFirstByName("Tori Amos").orElseThrow().getId());
    }

    @Test
    @Order(4)
    @Rollback(value = false)
    void testSuccessRepeated() {
        int oldArtifacts = artifactRepository.getAllByArtifactType(artifactType).size();
        assertThat(oldArtifacts).isGreaterThan(0);

        int oldMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(artifactType).size();
        assertThat(oldMediaFiles).isGreaterThan(0);
        assertThat(oldMediaFiles).isGreaterThanOrEqualTo(oldArtifacts);

        ProcessInfo pi = executeProcessor(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_OK);
        log.info("Music Loader Processing info: " + pi);
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Video music Loader"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts loaded"),
                        ProcessingStatus.INFO,
                        0,
                        null)
        );

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files loaded"),
                        ProcessingStatus.INFO,
                        0,
                        null)
        );

        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );

        int newArtifacts = artifactRepository.getAllByArtifactType(artifactType).size();
        assertThat(newArtifacts).isEqualTo(oldArtifacts);

        int newMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(artifactType).size();
        assertThat(newMediaFiles).isEqualTo(oldMediaFiles);
    }

    @Test
    @Order(5)
    @Rollback(value = false)
    void testSizeDuration() {
        artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .forEach(artifact -> {
                    assertThat(ValueValidator.isEmpty(artifact.getSize())).isFalse();
                    assertThat(ValueValidator.isEmpty(artifact.getDuration())).isFalse();
                });
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(10)
    @Rollback(value = false)
    void testWithTracksNoArtists() {
        prepareArtists();

        executeProcessor(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_TITLES_NO_ARTIST);
        var pi = processService.getProcessInfo();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Media files loaded"),
                        ProcessingStatus.INFO,
                        2,
                        null)
        );

        assertThat(pi.getProcessDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Tracks loaded"),
                        ProcessingStatus.INFO,
                        2,
                        null)
        );

        var artifacts = artifactRepository.findAll();
        assertThat(artifacts.size()).isEqualTo(1);
        assertThat(Optional.ofNullable(artifacts.getFirst().getArtist()).orElseThrow().getId()).isEqualTo(
                artistRepository.findFirstByName("The Cure").orElseThrow().getId()
        );

        var tracks = trackRepository.findAllFlatDTOByArtifactId(artifacts.getFirst().getId());
        assertThat(tracks.size()).isEqualTo(2);
        assertThat(tracks.getFirst().getArtistName()).isEqualTo("The Cure");
        assertThat(tracks.getFirst().getNum()).isEqualTo(1L);
        assertThat(tracks.getFirst().getDiskNum()).isNull();
        assertThat(tracks.getFirst().getTitle()).isEqualTo("Shake dog shake");
        assertThat(tracks.getFirst().getMediaFileName()).isEqualTo("01 Shake dog shake.mkv");

        processService.executeProcessor(ProcessorType.DV_MUSIC_VALIDATOR, tempDirs.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_TITLES_NO_ARTIST).toString());
        var validatePi = processService.getProcessInfo();
        assertThat(validatePi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(11)
    @Rollback(value = false)
    void testWithTracksAndArtists() {
        prepareArtists();

        executeProcessor(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_ARTISTS_AND_TITLE);
        var pi = processService.getProcessInfo();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        var artifacts = artifactRepository.findAll();
        assertThat(artifacts.size()).isEqualTo(1);
        assertThat(artifacts.get(0).getTitle()).isEqualTo("Beautiful Voices");
        assertThat(artifacts.get(0).getArtist()).isNull();

        var tracks = trackRepository.findAllFlatDTOByArtifactId(artifacts.get(0).getId());
        assertThat(tracks.size()).isEqualTo(3);

        assertThat(tracks.get(0).getNum()).isEqualTo(1L);
        assertThat(tracks.get(0).getArtistName()).isEqualTo("Nightwish");
        assertThat(tracks.get(0).getTitle()).isEqualTo("Ghost Love Score");

        assertThat(tracks.get(1).getNum()).isEqualTo(2L);
        assertThat(tracks.get(1).getArtistName()).isEqualTo("Mandragora Scream");
        assertThat(tracks.get(1).getTitle()).isEqualTo("Vision They Shared");

        assertThat(tracks.get(2).getNum()).isEqualTo(3L);
        assertThat(tracks.get(2).getArtistName()).isNull();
        assertThat(tracks.get(2).getTitle()).isEqualTo("Butterfly (Unsensored)");

        // set artist for artifact for validation
        Artist artist = new Artist();
        artist.setId(1L);
        artifacts.getFirst().setArtist(artist);
        artifactRepository.save(artifacts.getFirst());

        processService.executeProcessor(ProcessorType.DV_MUSIC_VALIDATOR, tempDirs.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_ARTISTS_AND_TITLE).toString());
        var validatePi = processService.getProcessInfo();
        assertThat(validatePi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(12)
    @Rollback(value = false)
    void testWithArtifactArtistVariableLength() {
        prepareArtists();

        executeProcessor(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_ARTIFACT_ARTIST_VARIABLE_LENGTH);
        var pi = processService.getProcessInfo();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        var artists = artistRepository.findAll();
        var artist = StreamSupport
                .stream(artists.spliterator(), false)
                .filter(a -> a.getName().equals("Black Sabbath"))
                .findFirst()
                .orElseThrow();

        var artifacts = artifactRepository.findAll();
        assertThat(artifacts.size()).isEqualTo(1);
        assertThat(artifacts.getFirst().getTitle()).isEqualTo("Black Sabbath - Videos");
        var artifactArtist = artifacts.getFirst().getArtist();
        assertThat(artifactArtist).isNotNull();
        assertThat(Optional.ofNullable(artifactArtist).orElseThrow().getId()).isEqualTo(artist.getId());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(13)
    @Rollback(value = false)
    @Disabled("Postponed because Therapy is written without a question mark")
    void testWithArtifactInvalidPathCharacter() {
        prepareArtists();

        executeProcessor(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_DV_TEST_WITH_ARTIFACT_INVALID_PATH_CHARACTER);
        var pi = processService.getProcessInfo();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        var artists = artistRepository.findAll();
        var artist = StreamSupport
                .stream(artists.spliterator(), false)
                .filter(a -> a.getName().equals("Therapy"))
                .findFirst()
                .orElseThrow();

        var artifacts = artifactRepository.findAll();
        assertThat(artifacts.size()).isEqualTo(1);
        assertThat(artifacts.getFirst().getTitle()).isEqualTo("Therapy - Videos");
        var artifactArtist = artifacts.getFirst().getArtist();
        assertThat(artifactArtist).isNotNull();
        assertThat(Optional.ofNullable(artifactArtist).orElseThrow().getId()).isEqualTo(artist.getId());
    }
}
