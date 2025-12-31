package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.generator.DataGenerator;
import com.romanpulov.odeonwss.generator.FileTreeGenerator;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateMP3Test {
    public static final ProcessorType PROCESSOR_TYPE = ProcessorType.MP3_VALIDATOR;
    public static final List<String> ARTIST_NAMES = List.of("Aerosmith", "Kosheen", "Various Artists");
    private ArtifactType artifactType;

    @Value("${test.data.path}")
    String testDataPath;

    private enum TestFolder {
        TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_OK,
        TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_WRONG_ARTIFACT_TITLE,
        TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_WRONG_COMPOSITION_TITLE,
        TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_WRONG_DUPLICATE_TRACK,
        TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_MISSING_ARTIST,
        TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_ADDITIONAL_ARTIST,
        TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_MISSING_ARTIFACT,
        TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_MISSING_TRACKS
    }

    private Map<TestFolder, Path> tempFolders;

    @BeforeAll
    public void setup() throws Exception {
        this.artifactType = artifactTypeRepository.getWithMP3();

        this.tempFolders = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_OK),
                this.testDataPath,
                """
{
    "Aerosmith": {
        "2004 Honkin'On Bobo": {
            "01 - Road Runner.mp3": "sample_mp3_1.mp3",
            "02 - Shame, Shame, Shame.mp3": "sample_mp3_1.mp3",
            "03 - Eyesight To The Blind.mp3": "sample_mp3_1.mp3",
            "04 - Baby, Please Don't Go.mp3": "sample_mp3_1.mp3",
            "05 - Never Loved A Girl.mp3": "sample_mp3_1.mp3",
            "06 - Back Back Train.mp3": "sample_mp3_1.mp3",
            "07 - You Gotta Move.mp3": "sample_mp3_1.mp3",
            "08 - The Grind.mp3": "sample_mp3_1.mp3",
            "09 - I'm Ready.mp3": "sample_mp3_1.mp3",
            "10 - Temperature.mp3": "sample_mp3_1.mp3",
            "11 - Stop Messin' Around.mp3": "sample_mp3_1.mp3",
            "12 - Jesus Is On The Main Line.mp3": "sample_mp3_1.mp3"
        }
    },
    "Kosheen": {
        "2004 Kokopelli": {
            "01 - Wasting My Time.mp3": "sample_mp3_1.mp3",
            "02 - All In My Head (Radio Edit).mp3": "sample_mp3_1.mp3",
            "03 - Crawling.mp3": "sample_mp3_1.mp3",
            "04 - Avalanche.mp3": "sample_mp3_1.mp3",
            "05 - Blue Eyed Boy.mp3": "sample_mp3_1.mp3",
            "06 - Suzy May.mp3": "sample_mp3_1.mp3",
            "07 - Swamp.mp3": "sample_mp3_1.mp3",
            "08 - Wish.mp3": "sample_mp3_1.mp3",
            "09 - Coming Home.mp3": "sample_mp3_1.mp3",
            "10 - Ages.mp3": "sample_mp3_1.mp3",
            "11 - Recovery.mp3": "sample_mp3_1.mp3",
            "12 - Little Boy.mp3": "sample_mp3_1.mp3"
        },
        "2007 Damage": {
            "01 - Damage.mp3": "sample_mp3_2.mp3",
            "02 - Overkill.mp3": "sample_mp3_2.mp3",
            "03 - Like A Book.mp3": "sample_mp3_2.mp3",
            "04 - Same Ground Again.mp3": "sample_mp3_2.mp3",
            "05 - Guilty.mp3": "sample_mp3_2.mp3",
            "06 - Chances.mp3": "sample_mp3_2.mp3",
            "07 - Out Of This World.mp3": "sample_mp3_2.mp3",
            "08 - Wish You Were Here.mp3": "sample_mp3_2.mp3",
            "09 - Thief.mp3": "sample_mp3_2.mp3",
            "10 - Under Fire.mp3": "sample_mp3_2.mp3",
            "11 - Not Enough Love.mp3": "sample_mp3_2.mp3",
            "12 - Cruel Heart.mp3": "sample_mp3_2.mp3",
            "13 - Professional Friend.mp3": "sample_mp3_2.mp3",
            "14 - Analogue Street Dub.mp3": "sample_mp3_2.mp3",
            "15 - Marching Orders.mp3": "sample_mp3_2.mp3",
            "16 - Your Life.mp3": "sample_mp3_2.mp3"
        }
    },
    "Various Artists": {
        "2000 Rock N' Roll Fantastic": {
            "001 - Simple Minds - Gloria.MP3": "sample_mp3_3.mp3",
            "002 - T. Jones - Jailhouse Rock.MP3": "sample_mp3_3.mp3"
        }
    }
}
                        """
        );

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_WRONG_ARTIFACT_TITLE),
                this.testDataPath,
                """
{
    "Aerosmith": {
        "2004  Honkin'On Bobo": {
            "01 - Road Runner.mp3": "sample_mp3_1.mp3",
            "02 - Shame, Shame, Shame.mp3": "sample_mp3_1.mp3",
            "03 - Eyesight To The Blind.mp3": "sample_mp3_1.mp3",
            "04 - Baby, Please Don't Go.mp3": "sample_mp3_1.mp3",
            "05 - Never Loved A Girl.mp3": "sample_mp3_1.mp3",
            "06 - Back Back Train.mp3": "sample_mp3_1.mp3",
            "07 - You Gotta Move.mp3": "sample_mp3_1.mp3",
            "08 - The Grind.mp3": "sample_mp3_1.mp3",
            "09 - I'm Ready.mp3": "sample_mp3_1.mp3",
            "10 - Temperature.mp3": "sample_mp3_1.mp3",
            "11 - Stop Messin' Around.mp3": "sample_mp3_1.mp3",
            "12 - Jesus Is On The Main Line.mp3": "sample_mp3_1.mp3"
        }
    }
}
                        """
        );

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_WRONG_COMPOSITION_TITLE),
                this.testDataPath,
                """
{
    "Aerosmith": {
        "2004 Honkin'On Bobo": {
            "01 - Road Runner.mp3": "sample_mp3_1.mp3",
            "02 - Shame, Shame, Shame.mp3": "sample_mp3_1.mp3",
            "03 - Eyesight To The Blind.mp3": "sample_mp3_1.mp3",
            "04-Baby, Please Don't Go.mp3": "sample_mp3_1.mp3"
        }
    }
}
                        """
        );

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_WRONG_DUPLICATE_TRACK),
                this.testDataPath,
                """
{
    "Aerosmith": {
        "2004 Honkin'On Bobo tracks": {
            "01 - Road Runner.mp3": "sample_mp3_1.mp3",
            "02 - Eyesight To The Blind.mp3": "sample_mp3_1.mp3",
            "02 - Shame, Shame, Shame.mp3": "sample_mp3_1.mp3"
        }
    }
}
                        """
        );

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_MISSING_ARTIST),
                this.testDataPath,
                """
{
    "Aerosmith": {
        "2004 Honkin'On Bobo": {
            "01 - Road Runner.mp3": "sample_mp3_4.mp3",
            "02 - Shame, Shame, Shame.mp3": "sample_mp3_3.mp3",
            "03 - Eyesight To The Blind.mp3": "sample_mp3_4.mp3",
            "04 - Baby, Please Don't Go.mp3": "sample_mp3_4.mp3",
            "05 - Never Loved A Girl.mp3": "sample_mp3_3.mp3",
            "06 - Back Back Train.mp3": "sample_mp3_4.mp3",
            "07 - You Gotta Move.mp3": "sample_mp3_6.mp3",
            "08 - The Grind.mp3": "sample_mp3_5.mp3",
            "09 - I'm Ready.mp3": "sample_mp3_4.mp3",
            "10 - Temperature.mp3": "sample_mp3_3.mp3",
            "11 - Stop Messin' Around.mp3": "sample_mp3_2.mp3",
            "12 - Jesus Is On The Main Line.mp3": "sample_mp3_3.mp3"
        }
    }
}
                        """
        );

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_ADDITIONAL_ARTIST),
                this.testDataPath,
                """
{
  "Accept": {
    "1983 Balls to the wall": {
      "01 - First.mp3": "sample_mp3_4.mp3"
    }
  },
  "Aerosmith": {
    "2004 Honkin'On Bobo": {
      "01 - Road Runner.mp3": "sample_mp3_6.mp3",
      "02 - Shame, Shame, Shame.mp3": "sample_mp3_2.mp3",
      "03 - Eyesight To The Blind.mp3": "sample_mp3_4.mp3",
      "04 - Baby, Please Don't Go.mp3": "sample_mp3_1.mp3",
      "05 - Never Loved A Girl.mp3": "sample_mp3_1.mp3",
      "06 - Back Back Train.mp3": "sample_mp3_5.mp3",
      "07 - You Gotta Move.mp3": "sample_mp3_2.mp3",
      "08 - The Grind.mp3": "sample_mp3_2.mp3",
      "09 - I'm Ready.mp3": "sample_mp3_1.mp3",
      "10 - Temperature.mp3": "sample_mp3_6.mp3",
      "11 - Stop Messin' Around.mp3": "sample_mp3_3.mp3",
      "12 - Jesus Is On The Main Line.mp3": "sample_mp3_3.mp3"
    }
  },
  "Kosheen": {
    "2004 Kokopelli": {
      "01 - Wasting My Time.mp3": "sample_mp3_2.mp3",
      "02 - All In My Head (Radio Edit).mp3": "sample_mp3_4.mp3",
      "03 - Crawling.mp3": "sample_mp3_5.mp3",
      "04 - Avalanche.mp3": "sample_mp3_3.mp3",
      "05 - Blue Eyed Boy.mp3": "sample_mp3_1.mp3",
      "06 - Suzy May.mp3": "sample_mp3_2.mp3",
      "07 - Swamp.mp3": "sample_mp3_4.mp3",
      "08 - Wish.mp3": "sample_mp3_6.mp3",
      "09 - Coming Home.mp3": "sample_mp3_6.mp3",
      "10 - Ages.mp3": "sample_mp3_5.mp3",
      "11 - Recovery.mp3": "sample_mp3_4.mp3",
      "12 - Little Boy.mp3": "sample_mp3_1.mp3"
    },
    "2007 Damage": {
      "01 - Damage.mp3": "sample_mp3_4.mp3",
      "02 - Overkill.mp3": "sample_mp3_5.mp3",
      "03 - Like A Book.mp3": "sample_mp3_6.mp3",
      "04 - Same Ground Again.mp3": "sample_mp3_5.mp3",
      "05 - Guilty.mp3": "sample_mp3_2.mp3",
      "06 - Chances.mp3": "sample_mp3_3.mp3",
      "07 - Out Of This World.mp3": "sample_mp3_2.mp3",
      "08 - Wish You Were Here.mp3": "sample_mp3_2.mp3",
      "09 - Thief.mp3": "sample_mp3_4.mp3",
      "10 - Under Fire.mp3": "sample_mp3_3.mp3",
      "11 - Not Enough Love.mp3": "sample_mp3_5.mp3",
      "12 - Cruel Heart.mp3": "sample_mp3_3.mp3",
      "13 - Professional Friend.mp3": "sample_mp3_2.mp3",
      "14 - Analogue Street Dub.mp3": "sample_mp3_1.mp3",
      "15 - Marching Orders.mp3": "sample_mp3_2.mp3",
      "16 - Your Life.mp3": "sample_mp3_4.mp3"
    }
  }
}
                        """
        );

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_MISSING_ARTIFACT),
                this.testDataPath,
                """
{
  "Aerosmith": {
    "2004 Honkin'On Bobo": {
      "01 - Road Runner.mp3": "sample_mp3_3.mp3",
      "02 - Shame, Shame, Shame.mp3": "sample_mp3_3.mp3",
      "03 - Eyesight To The Blind.mp3": "sample_mp3_6.mp3",
      "04 - Baby, Please Don't Go.mp3": "sample_mp3_2.mp3",
      "05 - Never Loved A Girl.mp3": "sample_mp3_5.mp3",
      "06 - Back Back Train.mp3": "sample_mp3_3.mp3",
      "07 - You Gotta Move.mp3": "sample_mp3_4.mp3",
      "08 - The Grind.mp3": "sample_mp3_5.mp3",
      "09 - I'm Ready.mp3": "sample_mp3_1.mp3",
      "10 - Temperature.mp3": "sample_mp3_1.mp3",
      "11 - Stop Messin' Around.mp3": "sample_mp3_1.mp3",
      "12 - Jesus Is On The Main Line.mp3": "sample_mp3_6.mp3"
    }
  },
  "Kosheen": {
    "2004 Kokopelli": {
      "01 - Wasting My Time.mp3": "sample_mp3_3.mp3",
      "02 - All In My Head (Radio Edit).mp3": "sample_mp3_5.mp3",
      "03 - Crawling.mp3": "sample_mp3_6.mp3",
      "04 - Avalanche.mp3": "sample_mp3_6.mp3",
      "05 - Blue Eyed Boy.mp3": "sample_mp3_4.mp3",
      "06 - Suzy May.mp3": "sample_mp3_3.mp3",
      "07 - Swamp.mp3": "sample_mp3_2.mp3",
      "08 - Wish.mp3": "sample_mp3_1.mp3",
      "09 - Coming Home.mp3": "sample_mp3_1.mp3",
      "10 - Ages.mp3": "sample_mp3_4.mp3",
      "11 - Recovery.mp3": "sample_mp3_1.mp3",
      "12 - Little Boy.mp3": "sample_mp3_1.mp3"
    }
  },
  "Various Artists": {
    "2000 Rock N' Roll Fantastic": {
      "130 - Simple Minds - Gloria.MP3": "sample_mp3_3_capital_ext.MP3",
      "131 - T. Jones - Jailhouse Rock.MP3": "sample_mp3_3_capital_ext.MP3"
    }
  }
}
                        """
        );

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_MISSING_TRACKS),
                this.testDataPath,
                """
{
  "Aerosmith": {
    "2004 Honkin'On Bobo": {
      "01 - Road Runner.mp3": "sample_mp3_1.mp3",
      "02 - Shame, Shame, Shame.mp3": "sample_mp3_1.mp3",
      "03 - Eyesight To The Blind.mp3": "sample_mp3_6.mp3",
      "04 - Baby, Please Don't Go.mp3": "sample_mp3_2.mp3",
      "05 - Never Loved A Girl.mp3": "sample_mp3_1.mp3",
      "06 - Back Back Train.mp3": "sample_mp3_4.mp3",
      "07 - You Gotta Move.mp3": "sample_mp3_4.mp3",
      "08 - The Grind.mp3": "sample_mp3_4.mp3",
      "09 - I'm Ready.mp3": "sample_mp3_2.mp3",
      "10 - Temperature.mp3": "sample_mp3_2.mp3",
      "11 - Stop Messin' Around.mp3": "sample_mp3_1.mp3",
      "12 - Jesus Is On The Main Line.mp3": "sample_mp3_2.mp3"
    }
  },
  "Kosheen": {
    "2004 Kokopelli": {
      "01 - Wasting My Time.mp3": "sample_mp3_6.mp3",
      "05 - Blue Eyed Boy.mp3": "sample_mp3_1.mp3",
      "06 - Suzy May.mp3": "sample_mp3_4.mp3",
      "07 - Swamp.mp3": "sample_mp3_6.mp3",
      "08 - Wish.mp3": "sample_mp3_3.mp3",
      "09 - Coming Home.mp3": "sample_mp3_3.mp3",
      "10 - Ages.mp3": "sample_mp3_6.mp3",
      "11 - Recovery.mp3": "sample_mp3_5.mp3",
      "12 - Little Boy.mp3": "sample_mp3_2.mp3"
    },
    "2007 Damage": {
      "01 - Damage.mp3": "sample_mp3_1.mp3",
      "02 - Overkill.mp3": "sample_mp3_5.mp3",
      "03 - Like A Book.mp3": "sample_mp3_6.mp3",
      "04 - Same Ground Again.mp3": "sample_mp3_2.mp3",
      "05 - Guilty.mp3": "sample_mp3_1.mp3",
      "06 - Chances.mp3": "sample_mp3_5.mp3",
      "07 - Out Of This World.mp3": "sample_mp3_1.mp3",
      "08 - Wish You Were Here.mp3": "sample_mp3_5.mp3",
      "09 - Thief.mp3": "sample_mp3_5.mp3",
      "10 - Under Fire.mp3": "sample_mp3_3.mp3",
      "11 - Not Enough Love.mp3": "sample_mp3_1.mp3",
      "12 - Cruel Heart.mp3": "sample_mp3_4.mp3",
      "13 - Professional Friend.mp3": "sample_mp3_6.mp3",
      "14 - Analogue Street Dub.mp3": "sample_mp3_4.mp3",
      "15 - Marching Orders.mp3": "sample_mp3_3.mp3",
      "16 - Your Life.mp3": "sample_mp3_3.mp3"
    }
  },
  "Various Artists": {
    "2000 Rock N' Roll Fantastic": {
      "001 - Simple Minds - Gloria.MP3": "sample_mp3_3_capital_ext.MP3",
      "002 - T. Jones - Jailhouse Rock.MP3": "sample_mp3_3_capital_ext.MP3"
    }
  }
}
                        """
        );

    }

    @AfterAll
    public void teardown() {
        FileTreeGenerator.deleteTempFiles(tempFolders.values());
    }

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

    final ProcessService service;

    @Autowired
    DataGenerator dataGenerator;

    @Autowired
    public ServiceProcessValidateMP3Test(ProcessService service) {
        this.service = service;
    }

    private void prepareArtists() {
        dataGenerator.createArtistsFromList(ARTIST_NAMES);
    }

    private void prepareInternal() {
        service.executeProcessor(
                ProcessorType.MP3_LOADER,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_OK).toString());
        /*
        DbManagerService.loadOrPrepare(databaseConfiguration, DbManagerService.DbType.DB_LOADED_MP3, () -> {
            service.executeProcessor(ProcessorType.MP3_LOADER, null);
            assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

            service.executeProcessor(ProcessorType.CLASSICS_IMPORTER);
            assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        });

         */
    }

    private ProcessInfo executeProcessor() {
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_OK).toString());
        return service.getProcessInfo();
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testLoadEmptyShouldFail() {
        this.prepareArtists();

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
    @Sql({"/schema.sql", "/data.sql"})
    void testLoad() {
        this.prepareArtists();
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
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_WRONG_ARTIFACT_TITLE).toString());
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
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_MISSING_ARTIST).toString());
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
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_ADDITIONAL_ARTIST).toString());
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
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_MISSING_ARTIFACT).toString());
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
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_MP3_MISSING_TRACKS).toString());
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
    @Sql({"/schema.sql", "/data.sql"})
    void validateOk() {
        this.prepareArtists();
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
    @Sql({"/schema.sql", "/data.sql"})
    void containsFilesShouldFail() {
        this.prepareArtists();
        this.prepareInternal();
        service.executeProcessor(
                PROCESSOR_TYPE,
                Paths.get(
                        tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_MP3_TEST_OK).toString(),
                        "Kosheen",
                        "2004 Kokopelli"
                ).toString());
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(processDetails.get(1).getInfo().getMessage()).contains("Expected directory, found");
        assertThat(processDetails.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artists not in files or have no artifacts and tracks",
                                ARTIST_NAMES),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(14)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactInDbShouldFail() {
        this.prepareArtists();
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
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactInFilesShouldFail() {
        this.prepareArtists();
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
    @Sql({"/schema.sql", "/data.sql"})
    void testNewFileInDbShouldFail() {
        this.prepareArtists();
        this.prepareInternal();
        Artifact artifact = artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Kokopelli") && !Objects.isNull(a.getYear()) && a.getYear().equals(2004L))
                .findFirst().orElseThrow();
        Track track = trackRepository
                .findByIdWithMediaFiles(artifact.getTracks().getFirst().getId())
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
    @Sql({"/schema.sql", "/data.sql"})
    void testNewFileInFilesShouldFail() {
        this.prepareArtists();
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
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactFileInDbShouldFail() {
        this.prepareArtists();
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
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactFileInFilesShouldFail() {
        this.prepareArtists();
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
        this.prepareArtists();
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
        this.prepareArtists();
        this.prepareInternal();

        Artifact artifact = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Damage"))
                .findFirst().orElseThrow();

        artifact.setSize(Optional.ofNullable(artifact.getSize()).orElse(0L) + 530);
        artifactRepository.save(artifact);

        executeProcessor();

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
        this.prepareArtists();
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
        this.prepareArtists();
        this.prepareInternal();

        Track track = trackRepository
                .getTracksByArtifactType(artifactType)
                .stream()
                .filter(t -> t.getTitle().equals("Thief"))
                .findFirst()
                .orElseThrow();

        track.setDuration(Optional.ofNullable(track.getDuration()).orElse(0L) + 5);
        trackRepository.save(track);

        executeProcessor();

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
