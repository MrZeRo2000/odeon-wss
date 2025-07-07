package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.generator.FileTreeGenerator;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceProcessLoadLATest {

    private static final Logger log = Logger.getLogger(ServiceProcessLoadLATest.class.getSimpleName());
    private static final ProcessorType PROCESSOR_TYPE = ProcessorType.LA_LOADER;
    private static final String[] ARTIST_LIST = {
            "Evanescence",
            "Pink Floyd",
            "Therapy",
            "Tori Amos",
            "Abigail Williams",
            "Agua De Annique",
            "Christina Aguilera",
            "The Sisters Of Mercy"
    };

    @Value("${test.data.path}")
    String testDataPath;

    private enum TestFolder {
        TF_SERVICE_PROCESS_LOAD_LA_TEST_OK
    }

    private Map<TestFolder, Path> tempFolders;

    @BeforeAll
    public void setup() throws Exception {
        this.tempFolders = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_LA_TEST_OK),
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
        log.info("After all");
        FileTreeGenerator.deleteTempFiles(tempFolders.values());
    }


    @Autowired
    private ProcessService processService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testNoArtists() {
        List<ProcessDetail> processDetail;

        // warnings - no artists exist
        processService.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_LA_TEST_OK).toString());
        processDetail = processService.getProcessInfo().getProcessDetails();

        Assertions.assertEquals(11, processDetail.size());
        Assertions.assertEquals(ProcessingStatus.WARNING, processService.getProcessInfo().getProcessingStatus());

        List<ProcessDetail> expectedProcessDetails = Stream.of(ARTIST_LIST)
                .map(v -> new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artist " + v + " not found"),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, v))
                )
                .collect(Collectors.toList());

        // check processing progress
        for (int i = 1; i < 9; i++) {
            ProcessingAction pa = processDetail.get(i).getProcessingAction();
            Assertions.assertNotNull(pa);
            Assertions.assertEquals(ProcessingActionType.ADD_ARTIST, pa.getActionType());
            Assertions.assertTrue(
                    pa.getValue().contains("Evanescence") ||
                            pa.getValue().contains("Pink Floyd") ||
                            pa.getValue().contains("Therapy") ||
                            pa.getValue().contains("Tori Amos") ||
                            pa.getValue().contains("Abigail Williams") ||
                            pa.getValue().contains("Agua De Annique") ||
                            pa.getValue().contains("Christina Aguilera") ||
                            pa.getValue().contains("The Sisters Of Mercy")
            );
            assertThat(processDetail.get(i)).isIn(expectedProcessDetails);
        }
    }

    void testSizeDuration(Artifact artifact, List<Track> tracks, List<MediaFile> mediaFiles, long size) {
        //duration
        assertThat(tracks.stream().collect(Collectors.summarizingLong(t -> Optional.ofNullable(t.getDuration()).orElseThrow())).getSum()).isEqualTo(
                mediaFiles.stream().collect(Collectors.summarizingLong(t -> Optional.ofNullable(t.getDuration()).orElseThrow())).getSum()
        );
        assertThat(artifact.getDuration()).isEqualTo(
                tracks.stream().collect(Collectors.summarizingLong(t -> Optional.ofNullable(t.getDuration()).orElseThrow())).getSum()
        );

        //size
        assertThat(artifact.getSize()).isEqualTo(
                mediaFiles.stream().collect(Collectors.summarizingLong(MediaFile::getSize)).getSum()
        );
        if (size > 0) {
            assertThat(artifact.getSize()).isEqualTo(size);
        }
    }

    void testOneMediaFilePerOneTrack(String artistName, String artifactName, int trackCount, long size) {
        log.info("testOneMediaFilePerOneTrack: artistName=" + artistName + ", artifactName=" + artifactName);
        Artist artist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, artistName).orElseThrow();
        List<Artifact> artifacts = artifactRepository
                .getArtifactsByArtist(artist)
                .stream()
                .filter(a -> a.getTitle().equals(artifactName))
                .toList();
        Assertions.assertEquals(1, artifacts.size());

        List<MediaFile> mediaFiles = mediaFileRepository.findAllByArtifactId(artifacts.getFirst().getId());
        Assertions.assertEquals(trackCount, mediaFiles.size());

        List<Track> tracks = trackRepository.findAllByArtifact(artifacts.getFirst());
        Assertions.assertEquals(trackCount, tracks.size());

        testSizeDuration(artifacts.getFirst(), tracks, mediaFiles, size);
    }

    void testOneMediaFilePerAllTracks(String artistName, String artifactName, int trackCount, int diskCount, long size) {
        Artist artist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, artistName).orElseThrow();
        List<Artifact> artifacts = artifactRepository
                .getArtifactsByArtist(artist)
                .stream()
                .filter(a -> a.getTitle().equals(artifactName))
                .toList();
        Assertions.assertEquals(1, artifacts.size());

        List<MediaFile> mediaFiles = mediaFileRepository.findAllByArtifactId(artifacts.getFirst().getId());
        Assertions.assertEquals(diskCount, mediaFiles.size());

        List<Track> tracks = trackRepository.findAllByArtifact(artifacts.getFirst());
        Assertions.assertEquals(trackCount, tracks.size());

        if (size > 0) {
            testSizeDuration(artifacts.getFirst(), tracks, mediaFiles, size);
        }
    }

    @Test
    @Order(2)
    @Sql({"/schema.sql", "/data.sql"})
    void testOk() {
        List.of(ARTIST_LIST)
                .forEach(s -> artistRepository.save(
                        new EntityArtistBuilder().withType(ArtistType.ARTIST).withName(s).build()
                ));

        processService.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_LA_TEST_OK).toString());

        ProcessInfo pi = processService.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        int item = 0;
        assertThat(processDetails.get(item++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started LA Loader"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(item++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists loaded"),
                        ProcessingStatus.INFO,
                        8,
                        null)
        );

        assertThat(processDetails.get(item++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts loaded"),
                        ProcessingStatus.INFO,
                        10,
                        null)
        );

        assertThat(processDetails.get(item++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Tracks loaded"),
                        ProcessingStatus.INFO,
                        117,
                        null)
        );

        assertThat(processDetails.get(item)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );

        testOneMediaFilePerOneTrack("Abigail Williams", "In The Absence Of Light", 8, 4*12358748 + 4*1565617);
        testOneMediaFilePerAllTracks("Agua De Annique", "Air", 13, 1, 1565617L);
        testOneMediaFilePerOneTrack("Christina Aguilera", "Back To Basics", 22, 14*12358748 + 8*1565617);
        testOneMediaFilePerAllTracks("Evanescence", "Origin", 11, 1, 12358748);
        testOneMediaFilePerOneTrack("Evanescence", "Evanescence", 16, 7*12358748 + 9*1565617);
        testOneMediaFilePerAllTracks("Pink Floyd", "Delicate Sound Of Thunder", 15, 2, 12358748 + 1565617);
        testOneMediaFilePerOneTrack("The Sisters Of Mercy", "Anaconda 7 Inch Single", 1, 1745574);
        testOneMediaFilePerAllTracks("Therapy", "Nurse", 10, 1, 2462664);
        testOneMediaFilePerAllTracks("Therapy", "Infernal Love", 11, 1, 2462664);
        testOneMediaFilePerAllTracks("Tori Amos", "Y Kant Tori Read", 10, 1, 13577349);
    }
}