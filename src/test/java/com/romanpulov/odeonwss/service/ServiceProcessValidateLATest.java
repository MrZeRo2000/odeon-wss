package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.generator.DataGenerator;
import com.romanpulov.odeonwss.generator.FileTreeGenerator;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceProcessValidateLATest {
    private static final List<String> TEST_ARTISTS =
            List.of(
                    "Evanescence",
                    "Pink Floyd",
                    "Therapy",
                    "Tori Amos",
                    "Abigail Williams",
                    "Agua De Annique",
                    "Christina Aguilera",
                    "The Sisters Of Mercy");

    @Value("${test.data.path}")
    String testDataPath;

    private enum TestFolder {
        TF_SERVICE_PROCESS_VALIDATE_LA_TEST_OK
    }

    private Map<TestFolder, Path> tempFolders;

    @BeforeAll
    public void setup() throws Exception {
        this.artifactType = artifactTypeRepository.getWithLA();

        this.tempFolders = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_LA_TEST_OK),
                this.testDataPath,
                """
{
  "Abigail Williams": {
    "2010 In The Absence Of Light": {
      "01 Hope The Great Betrayal.flac": "sample_flac_1.flac",
      "02 Final Destiny Of The Gods.flac": "sample_flac_2.flac",
      "03 The Mysteries That Bind The Flesh.flac": "sample_flac_2.flac",
      "04 Infernal Divide.flac": "sample_flac_1.flac",
      "05 In Death Comes The Great Silence.flac": "sample_flac_2.flac",
      "06 What Hells Await Me.flac": "sample_flac_1.flac",
      "07 An Echo In Our Legends.flac": "sample_flac_2.flac",
      "08 Malediction.flac": "sample_flac_1.flac",
      "Abigail Williams - In The Absence Of Light.log": "sample.log",
      "Abigail Williams - In The Absence Of Light.m3u": "sample.m3u",
      "Front.jpg": "sample.jpg",
      "In The Absence Of Light.CUE": "In The Absence Of Light.CUE"
    }
  },
  "Agua De Annique": {
    "2007 Air": {
      "air.flac": "sample_flac_2.flac",
      "air.flac.cue": "air.flac.cue",
      "air.wav.cue": "Celestial Completion.cue",
      "air.wav.log": "sample.log",
      "air.wav.toc": "sample.toc",
      "scans": {
        "back0000.jpg": "sample.jpg",
        "book0000.jpg": "sample.jpg",
        "book0001.jpg": "sample.jpg",
        "book0002.jpg": "sample.jpg",
        "book0003.jpg": "sample.jpg",
        "book0004.jpg": "sample.jpg",
        "book0005.jpg": "sample.jpg",
        "book0006.jpg": "sample.jpg",
        "book0007.jpg": "sample.jpg",
        "cd.jpg": "sample.jpg",
        "front.jpg": "sample.jpg",
        "sticker.jpg": "sample.jpg"
      }
    }
  },
  "Christina Aguilera": {
    "2006 Back To Basics": {
      "CD1": {
        "01 - Intro (Back To Basics).flac": "sample_flac_2.flac",
        "02 - Makes Me Wanna Pray (feat. Steve Winwood).flac": "sample_flac_1.flac",
        "03 - Back In The Day.flac": "sample_flac_2.flac",
        "04 - Ain't No Other Man.flac": "sample_flac_2.flac",
        "05 - Understand.flac": "sample_flac_1.flac",
        "06 - Slow Down Baby.flac": "sample_flac_1.flac",
        "07 - Oh Mother.flac": "sample_flac_1.flac",
        "08 - F.U.S.S..flac": "sample_flac_1.flac",
        "09 - On Our Way.flac": "sample_flac_2.flac",
        "10 - Without You.flac": "sample_flac_2.flac",
        "11 - Still Dirrty.flac": "sample_flac_1.flac",
        "12 - Here To Stay.flac": "sample_flac_2.flac",
        "13 - Thank You (Dedication To Fans...).flac": "sample_flac_2.flac",
        "Christina Aguilera - Back To Basics (Disc 1).log": "sample.log",
        "Front.jpg": "sample.jpg"
      },
      "CD2": {
        "01 - Enter The Circus.flac": "sample_flac_1.flac",
        "02 - Welcome.flac": "sample_flac_1.flac",
        "03 - Candyman.flac": "sample_flac_2.flac",
        "04 - Nasty Naughty Boy.flac": "sample_flac_1.flac",
        "05 - I Got Trouble.flac": "sample_flac_1.flac",
        "06 - Hurt.flac": "sample_flac_1.flac",
        "07 - Mercy On Me.flac": "sample_flac_1.flac",
        "08 - Save Me From Myself.flac": "sample_flac_1.flac",
        "09 - The Right Man.flac": "sample_flac_1.flac",
        "Christina Aguilera - Back To Basics (Disc-2).log": "sample.log",
        "Front.jpg": "sample.jpg"
      }
    }
  },
  "Evanescence": {
    "2000 Origin": {
      "Flac.log": "sample.log",
      "Origin.cue": "Origin.cue",
      "Origin.flac": "sample_flac_1.flac",
      "Origin.log": "sample.log",
      "scans": {
        "Back.jpg": "sample.jpg",
        "Book.jpg": "sample.jpg",
        "Front2.jpg": "sample.jpg",
        "Origin.jpg": "sample.jpg"
      }
    },
    "2011 Evanescence": {
      "01 - What You Want.flac": "sample_flac_2.flac",
      "02 - Made Of Stone.flac": "sample_flac_2.flac",
      "03 - The Change.flac": "sample_flac_2.flac",
      "04 - My Heart Is Broken.flac": "sample_flac_1.flac",
      "05 - The Other Side.flac": "sample_flac_1.flac",
      "06 - Erase This.flac": "sample_flac_2.flac",
      "07 - Lost In Paradise.flac": "sample_flac_1.flac",
      "08 - Sick.flac": "sample_flac_2.flac",
      "09 - End Of The Dream.flac": "sample_flac_2.flac",
      "10 - Oceans.flac": "sample_flac_2.flac",
      "11 - Never Go Back.flac": "sample_flac_1.flac",
      "12 - Swimming Home.flac": "sample_flac_1.flac",
      "13 - New Way To Bleed.flac": "sample_flac_1.flac",
      "14 - Say You Will.flac": "sample_flac_2.flac",
      "15 - Disappear.flac": "sample_flac_1.flac",
      "16 - Secret Door.flac": "sample_flac_2.flac",
      "Evanescence - Evanescence.log": "sample.log",
      "folder.jpg": "sample.jpg"
    }
  },
  "Pink Floyd": {
    "1988 Delicate Sound Of Thunder": {
      "Pink Floyd - Delicate Sound of Thunder CD1.cue": "Thunder CD1.cue",
      "Pink Floyd - Delicate Sound of Thunder CD1.flac": "sample_flac_1.flac",
      "Pink Floyd - Delicate Sound of Thunder CD2.cue": "Thunder CD2.cue",
      "Pink Floyd - Delicate Sound of Thunder CD2.flac": "sample_flac_2.flac",
      "Scan": {
        "Back.jpg": "sample.jpg"
      },
      "Отчет ЕАС 1.txt": "sample.txt",
      "Отчет ЕАС 2.txt": "sample.txt"
    }
  },
  "The Sisters Of Mercy": {
    "1983 Anaconda 7 Inch Single": {
      "01 - Anaconda.m4a": "sample.m4a"
    }
  },
  "Therapy": {
    "1993 Nurse": {
      "Back.jpg": "sample.jpg",
      "Front.jpg": "sample.jpg",
      "log.log": "sample.log",
      "Nurse.log": "sample.log",
      "Therapy - Nurse.ape": "sample.ape",
      "Therapy - Nurse.cue": "Nurse.cue"
    },
    "1995 Infernal Love": {
      "Covers": {
        "back.jpg": "sample.jpg",
        "cd.jpg": "sample.jpg",
        "front.jpg": "sample.jpg",
        "inlay.jpg": "sample.jpg"
      },
      "Infernal Love.ape": "sample.ape",
      "Infernal Love.cue": "Infernal Love.cue",
      "Infernal Love.LOG": "sample_capital_ext.LOG"
    }
  },
  "Tori Amos": {
    "1988 Y Kant Tori Read": {
      "ReleaseInfo": {
        "cover_preview_290.jpg": "sample.jpg",
        "LOG_1988_Y_Kant_Tori_Read.gif": "sample.gif"
      },
      "scans": {
        "cover_preview_290.jpg": "sample.jpg"
      },
      "Tori Amos - Y Kant Tori Read.cue": "Celestial Completion.cue",
      "Tori Amos - Y Kant Tori Read.wv": "sample.wv",
      "Tori Amos - Y Kant Tori Read.wv.cue": "Y Kant Tori Read.wv.cue",
      "Y Kant Tori Read.log": "sample.log"
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
    DataGenerator dataGenerator;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ProcessService service;

    @Autowired
    EntityManager em;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    private ArtifactType artifactType;

    private void prepareInternal() {
        dataGenerator.createArtistsFromList(TEST_ARTISTS);

        service.executeProcessor(
                ProcessorType.LA_LOADER,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_LA_TEST_OK).toString());
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        /*
        DbManagerService.loadOrPrepare(databaseConfiguration, DbManagerService.DbType.DB_LOADED_LA, () -> {
            TEST_ARTISTS
                    .forEach(s -> artistRepository.save(
                            new EntityArtistBuilder().withType(ArtistType.ARTIST).withName(s).build()
                    ));

            service.executeProcessor(ProcessorType.LA_LOADER, null);
            assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        });

         */
    }

    private ProcessInfo executeProcessor() {
        service.executeProcessor(
                ProcessorType.LA_VALIDATOR,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_LA_TEST_OK).toString());
        return service.getProcessInfo();
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testEmptyShouldFail() {
        dataGenerator.createArtistsFromList(TEST_ARTISTS);

        ProcessInfo pi = executeProcessor();

        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(processDetails.get(0)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started LA Validator"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );
        assertThat(processDetails.get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artists not in database or have no artifacts and tracks",
                                TEST_ARTISTS.stream().sorted().collect(Collectors.toList())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
        assertThat(processDetails.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(2)
    void testLoad() {
        service.executeProcessor(
                ProcessorType.LA_LOADER,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_LA_TEST_OK).toString());
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
    }

    @Test
    @Order(3)
    void testOk() {
        ProcessInfo pi = executeProcessor();

        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started LA Validator"),
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

    private String getErrorStringFromTrackId(long id) {
        Track track = trackRepository.findByIdWithMediaFiles(id).orElseThrow();
        Artifact artifact = artifactRepository.findById(track.getArtifact().getId()).orElseThrow();
        assert artifact.getArtist() != null;
        Artist artist = artistRepository.findById(artifact.getArtist().getId()).orElseThrow();
        return artist.getName() + " >> " +
               artifact.getYear() + " " + artifact.getTitle() + " >> " +
               track.getMediaFiles().stream().findFirst().orElseThrow().getName();
    }

    @Test
    @Order(4)
    @Transactional
    void testNoTrackMediaFileShouldFail() {
        String errorString = getErrorStringFromTrackId(1L);

        em.createNativeQuery("delete from tracks_media_files WHERE trck_id = 1").executeUpdate();

        ProcessInfo pi = executeProcessor();

        List<ProcessDetail> processDetails = pi.getProcessDetails();
        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started LA Validator"),
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
        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in database",
                                List.of(errorString)),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(5)
    @Transactional
    void testNoTrackShouldFail() {
        String errorString = getErrorStringFromTrackId(1L);

        em.createNativeQuery("delete from tracks WHERE trck_id = 1").executeUpdate();

        ProcessInfo pi = executeProcessor();

        List<ProcessDetail> processDetails = pi.getProcessDetails();
        int id = 0;

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started LA Validator"),
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
        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in database",
                                List.of(errorString)),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(6)
    void testOkAgain() {
        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
    }

    @Test
    @Order(11)
    @Sql({"/schema.sql", "/data.sql"})
    void validateOk() {
        this.prepareInternal();
        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started LA Validator"),
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
        this.prepareInternal();
        service.executeProcessor(
                ProcessorType.LA_VALIDATOR,
                Path.of(
                        tempFolders.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_LA_TEST_OK).toString(),
                        "Therapy",
                        "1993 Nurse").toString());
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(processDetails.get(1).getInfo().getMessage()).contains("Expected directory, found");
        assertThat(processDetails.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artists not in files or have no artifacts and tracks",
                                TEST_ARTISTS.stream().sorted().collect(Collectors.toList())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(14)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactInDbShouldFail() {
        this.prepareInternal();
        ArtifactType artifactType = artifactTypeRepository.getWithLA();

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
        this.prepareInternal();
        Artifact artifact = artifactRepository
                .findAll()
                .stream()
                .filter(a -> a.getTitle().equals("Origin"))
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
                                List.of("Evanescence >> 2000 Origin")),
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
        this.prepareInternal();
        ArtifactType artifactType = artifactTypeRepository.getWithLA();

        Artifact artifact = artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("Evanescence") && !Objects.isNull(a.getYear()) && a.getYear().equals(2011L))
                .findFirst().orElseThrow();
        Track track = trackRepository
                .findByIdWithMediaFiles(artifact.getTracks().getFirst().getId())
                .orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("New music file.flac");
        mediaFile.setFormat("FLAC");
        mediaFile.setSize(29883733L);
        mediaFile.setBitrate(1048L);
        mediaFileRepository.save(mediaFile);

        track.getMediaFiles().add(mediaFile);
        trackRepository.save(track);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in files",
                                List.of("Evanescence >> 2011 Evanescence >> " + mediaFile.getName())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(17)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewFileInFilesShouldFail() {
        this.prepareInternal();
        ArtifactType artifactType = artifactTypeRepository.getWithLA();

        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("16 - Secret Door.flac"))
                .findFirst()
                .orElseThrow();
        mediaFileRepository.delete(mediaFile);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files not in database",
                                List.of("Evanescence >> 2011 Evanescence >> " + mediaFile.getName())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(processDetails.get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact media files not in database",
                                List.of("Evanescence >> 2011 Evanescence >> " + mediaFile.getName())),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(18)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactFileInDbShouldFail() {
        this.prepareInternal();
        ArtifactType artifactType = artifactTypeRepository.getWithLA();

        Artifact artifact = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getTitle().equals("In The Absence Of Light"))
                .findFirst().orElseThrow();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName("Another media file.flac");
        mediaFile.setFormat("FLAC");
        mediaFile.setSize(160453L);
        mediaFile.setBitrate(987L);
        mediaFile.setArtifact(artifact);
        mediaFileRepository.save(mediaFile);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact media files not in files",
                                List.of("Abigail Williams >> 2010 In The Absence Of Light >> Another media file.flac")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(19)
    @Sql({"/schema.sql", "/data.sql"})
    void testNewArtifactFileInFilesShouldFail() {
        this.prepareInternal();
        ArtifactType artifactType = artifactTypeRepository.getWithLA();

        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(artifactType)
                .stream()
                .filter(m -> m.getName().equals("01 Hope The Great Betrayal.flac"))
                .findFirst()
                .orElseThrow();
        mediaFile.setArtifact(null);
        mediaFileRepository.save(mediaFile);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact media files not in database",
                                List.of("Abigail Williams >> 2010 In The Absence Of Light >> 01 Hope The Great Betrayal.flac")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(20)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(value = false)
    void testNoMonotonicallyIncreasingTrackNumbersShouldFail() {
        this.prepareInternal();
        ArtifactType artifactType = artifactTypeRepository.getWithLA();

        List<Track> tracks = trackRepository.getTracksByArtifactType(artifactType);

        Track track7 = tracks
                .stream()
                .filter(t -> (t.getNum() != null) && (t.getNum() == 7L))
                .findFirst()
                .orElseThrow();

        Track track8 = tracks
                .stream()
                .filter(t -> (t.getNum() != null) && (t.getNum() == 8L))
                .findFirst()
                .orElseThrow();

        track7.setNum(8L);
        track8.setNum(9L);

        trackRepository.save(track8);
        trackRepository.save(track7);

        ProcessInfo pi = executeProcessor();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetails.get(8)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Track numbers for artifact not increasing monotonically",
                                List.of("Abigail Williams >> 2010 In The Absence Of Light")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(21)
    @Sql({"/schema.sql", "/data.sql"})
    void testMediaFileSizeDifferentShouldFail() {
        this.prepareInternal();

        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(artifactTypeRepository.getWithLA())
                .stream()
                .filter(m -> m.getName().equals("06 What Hells Await Me.flac"))
                .findFirst()
                .orElseThrow();
        mediaFile.setSize(mediaFile.getSize() + 200L);
        mediaFileRepository.save(mediaFile);

        Artifact artifact = mediaFile.getArtifact();
        artifact.setSize(Optional.ofNullable(artifact.getSize()).orElse(0L) + 200L);
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(7)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Media files size mismatch",
                                List.of("Abigail Williams >> 2010 In The Absence Of Light >> 06 What Hells Await Me.flac")),
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

        Artifact artifact = artifactRepository.getAllByArtifactType(artifactTypeRepository.getWithLA())
                .stream()
                .filter(a -> a.getTitle().equals("Back To Basics"))
                .findFirst().orElseThrow();

        artifact.setSize(Optional.ofNullable(artifact.getSize()).orElse(0L) + 540);
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact size does not match media files size",
                                List.of("Christina Aguilera >> 2006 Back To Basics")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(23)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactMediaFileDurationDifferentShouldFail() {
        this.prepareInternal();

        Artifact artifact = artifactRepository.getAllByArtifactType(artifactTypeRepository.getWithLA())
                .stream()
                .filter(a -> a.getTitle().equals("Back To Basics"))
                .findFirst().orElseThrow();

        artifact.setDuration(Optional.ofNullable(artifact.getDuration()).orElse(0L) + 540);
        artifactRepository.save(artifact);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(6)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact duration does not match media files duration",
                                List.of("Christina Aguilera >> 2006 Back To Basics")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }


    @Test
    @Order(24)
    @Sql({"/schema.sql", "/data.sql"})
    void testArtifactTrackDurationDifferentShouldFail() {
        this.prepareInternal();

        Track track = trackRepository
                .getTracksByArtifactType(artifactType)
                .stream()
                .filter(t -> t.getTitle().equals("My Immortal"))
                .findFirst()
                .orElseThrow();

        track.setDuration(Optional.ofNullable(track.getDuration()).orElse(0L) + 5);
        trackRepository.save(track);

        ProcessInfo pi = executeProcessor();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(pi.getProcessDetails().get(9)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Artifact duration does not match tracks duration",
                                List.of("Evanescence >> 2000 Origin")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }
}
