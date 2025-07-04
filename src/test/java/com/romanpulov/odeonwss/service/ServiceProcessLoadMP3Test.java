package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.Track;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceProcessLoadMP3Test {

    private static final Logger log = Logger.getLogger(ServiceProcessLoadMP3Test.class.getSimpleName());
    public static final ProcessorType PROCESSOR_TYPE = ProcessorType.MP3_LOADER;

    @Value("${test.data.path}")
    String testDataPath;

    private enum TestFolder {
        TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_OK,
        TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_WRONG_ARTIFACT_TITLE,
        TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_WRONG_COMPOSITION_TITLE,
        TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_WRONG_DUPLICATE_TRACK
    }

    private  Map<TestFolder, Path> tempFolders;

    @BeforeAll
    public void setup() throws Exception {
        this.tempFolders = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_OK),
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
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_WRONG_ARTIFACT_TITLE),
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
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_WRONG_COMPOSITION_TITLE),
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
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_WRONG_DUPLICATE_TRACK),
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
    }

    @AfterAll
    public void teardown() {
        log.info("After all");
        FileTreeGenerator.deleteTempFiles(tempFolders.values());
    }

    @Autowired
    ProcessService service;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;


    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testOk() {
        Arrays.asList("Aerosmith", "Kosheen", "Various Artists").forEach(s ->
                artistRepository.save(
                        new EntityArtistBuilder()
                                .withType(ArtistType.ARTIST)
                                .withName(s)
                                .build()
                ));

        log.info("Created artists");
        Assertions.assertDoesNotThrow(() -> service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_OK).toString()));

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        assertThat(pi.getProcessDetails().get(0)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started MP3 Loader"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists loaded"),
                        ProcessingStatus.INFO,
                        3,
                        null)
        );


        assertThat(pi.getProcessDetails().get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts loaded"),
                        ProcessingStatus.INFO,
                        4,
                        null)
        );

        assertThat(pi.getProcessDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Tracks loaded"),
                        ProcessingStatus.INFO,
                        42,
                        null)
        );

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );

        Artist aerosmithArtist = artistRepository.findFirstByName("Aerosmith").orElseThrow();
        artistRepository.findFirstByName("Kosheen").orElseThrow();
        artistRepository.findFirstByName("Various Artists").orElseThrow();

        Artifact honkinArtifact = artifactRepository.getArtifactsByArtist(aerosmithArtist).getFirst();
        Assertions.assertEquals(trackRepository.findAllByArtifact(honkinArtifact).size(),
                mediaFileRepository.findAllByArtifactId(honkinArtifact.getId()).size());
    }

    @Test
    @Order(2)
    void testLoadMissingTracksOk() {
        Artist artist = artistRepository.findFirstByName("Aerosmith").orElseThrow();
        Artifact artifact = artifactRepository.getArtifactsByArtist(artist).getFirst();
        List<Track> tracks = trackRepository.findAllByArtifact(artifact);
        assertThat(tracks.size()).isEqualTo(12);
        assertThat(mediaFileRepository.findAllByArtifactId(artifact.getId()).size()).isEqualTo(12);

        mediaFileRepository.findFirstByArtifact(artifact).orElseThrow().setName("a different name");

        //delete tracks
        trackRepository.deleteAll(tracks);
        tracks = trackRepository.findAllByArtifact(artifact);
        assertThat(tracks.size()).isEqualTo(0);
        assertThat(mediaFileRepository.findAllByArtifactId(artifact.getId()).size()).isEqualTo(12);

        Assertions.assertDoesNotThrow(() -> service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_OK).toString()));
        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        tracks = trackRepository.findAllByArtifact(artifact);
        assertThat(tracks.size()).isEqualTo(12);
        assertThat(mediaFileRepository.findAllByArtifactId(artifact.getId()).size()).isEqualTo(12);
    }

    @Test
    @Order(3)
    @Sql({"/schema.sql", "/data.sql"})
    void testNoArtistExistsShouldFail() throws Exception {
        List<ProcessDetail> processDetail;

        // warnings - no artists exist
        service.executeProcessor(PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_OK).toString());

        ProcessInfo pi = service.getProcessInfo();
        processDetail = pi.getProcessDetails();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.WARNING);

        assertThat(pi.getProcessDetails().get(1)).isIn(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artist Aerosmith not found"),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Aerosmith")),
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artist Kosheen not found"),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Kosheen")),
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artist Various Artists not found"),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Various Artists"))
        );

        assertThat(pi.getProcessDetails().get(2)).isIn(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artist Aerosmith not found"),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Aerosmith")),
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artist Kosheen not found"),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Kosheen")),
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artist Various Artists not found"),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Various Artists"))
        );

        assertThat(pi.getProcessDetails().get(3)).isIn(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artist Aerosmith not found"),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Aerosmith")),
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artist Kosheen not found"),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Kosheen")),
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artist Various Artists not found"),
                        ProcessingStatus.WARNING,
                        null,
                        new ProcessingAction(ProcessingActionType.ADD_ARTIST, "Various Artists"))
        );

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists loaded"),
                        ProcessingStatus.INFO,
                        0,
                        null)
        );

        assertThat(pi.getProcessDetails().get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.WARNING,
                        null,
                        null)
        );


        // check processing progress
        ProcessingAction pa = processDetail.get(1).getProcessingAction();
        Assertions.assertNotNull(pa);
        Assertions.assertEquals(ProcessingActionType.ADD_ARTIST, pa.getActionType());
        Assertions.assertTrue(pa.getValue().contains("Aerosmith") || pa.getValue().contains("Kosheen"));

        //Validate DTO
        var dto = service.getById(1L);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getProcessDetails().get(1).getStatus()).isEqualTo(ProcessingStatus.WARNING);
        assertThat(dto.getProcessDetails().get(1).getProcessingAction().getActionType()).isEqualTo(ProcessingActionType.ADD_ARTIST);
        assertThat(dto.getProcessDetails().get(1).getProcessingAction().getValue()).isEqualTo("Aerosmith");
    }

    @Test
    @Order(4)
    @Sql({"/schema.sql", "/data.sql"})
    void testNoPathExistsShouldFail() {
        List<ProcessDetail> processDetail;

        // error - path not exist
        service.executeProcessor(PROCESSOR_TYPE, "non_existing_path");
        processDetail = service.getProcessInfo().getProcessDetails();
        Assertions.assertEquals(3, processDetail.size());
        Assertions.assertEquals(ProcessingStatus.FAILURE, processDetail.get(service.getProcessInfo().getProcessDetails().size() - 1).getStatus());
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(5)
    void testDirectoryWithFiles() {
        service.executeProcessor(PROCESSOR_TYPE, "");
        Assertions.assertEquals(ProcessingStatus.FAILURE, service.getProcessInfo().getProcessingStatus());
        Assertions.assertTrue(service.getProcessInfo().getProcessDetails().stream().anyMatch(
                p -> p.getInfo().getMessage().contains("directory, found:")));
    }

    @Test
    @Order(6)
    void testWrongArtifactTitle() throws Exception {
        Artist artist = new Artist();
        artist.setType(ArtistType.ARTIST);
        artist.setName("Aerosmith");

        artistRepository.save(artist);

        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_WRONG_ARTIFACT_TITLE).toString());
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetail = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetail.get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists loaded"),
                        ProcessingStatus.INFO,
                        1,
                        null)
        );

        assertThat(processDetail.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Error parsing artifact name",
                                List.of("Aerosmith >> 2004  Honkin'On Bobo")),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        assertThat(pi.getProcessDetails().get(3)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts loaded"),
                        ProcessingStatus.INFO,
                        0,
                        null)
        );

        assertThat(pi.getProcessDetails().get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        //Assertions.assertTrue(service.getProcessInfo().getProcessDetails().get(service.getProcessInfo().getProcessDetails().size() - 2).getInfo().getMessage().contains("Error parsing artifact name"));
        var dto = service.getById(3L);
        assertThat(dto.getProcessDetails().get(2).getMessage()).isEqualTo("Error parsing artifact name");
        assertThat(dto.getProcessDetails().get(2).getItems()).isEqualTo(List.of("Aerosmith >> 2004  Honkin'On Bobo"));
    }

    @Test
    @Order(7)
    void testWrongCompositionTitle() {
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_WRONG_COMPOSITION_TITLE).toString());
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetail = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetail.get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists loaded"),
                        ProcessingStatus.INFO,
                        1,
                        null)
        );


        assertThat(processDetail.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts loaded"),
                        ProcessingStatus.INFO,
                        1,
                        null)
        );

        String trackNameMessage = processDetail.get(3).getInfo().getMessage();
        assertThat(trackNameMessage).startsWith("Error parsing music track name");
        assertThat(trackNameMessage).contains("04-Baby, Please Don't Go.mp3");
        assertThat(trackNameMessage).contains("Aerosmith");
        assertThat(trackNameMessage).contains("2004 Honkin'On Bobo");

        assertThat(pi.getProcessDetails().get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(8)
    void testWrongDuplicateTrack() {
        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_WRONG_DUPLICATE_TRACK).toString());
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetail = pi.getProcessDetails();

        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        assertThat(processDetail.get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artists loaded"),
                        ProcessingStatus.INFO,
                        1,
                        null)
        );


        assertThat(processDetail.get(2)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts loaded"),
                        ProcessingStatus.INFO,
                        1,
                        null)
        );

        String trackNameMessage = processDetail.get(3).getInfo().getMessage();
        assertThat(trackNameMessage).startsWith("Duplicate track number for");
        assertThat(trackNameMessage).containsAnyOf("02 - Eyesight To The Blind.mp3", "02 - Shame, Shame, Shame.mp3");
        assertThat(trackNameMessage).contains("Aerosmith");
        assertThat(trackNameMessage).contains("2004 Honkin'On Bobo");

        assertThat(processDetail.get(4)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Tracks loaded"),
                        ProcessingStatus.INFO,
                        0,
                        null)
        );

        assertThat(pi.getProcessDetails().get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );
    }

    @Test
    @Order(9)
    @Sql({"/schema.sql", "/data.sql"})
    void testOneArtistNotExists() {
        artistRepository.save(
                new EntityArtistBuilder()
                        .withType(ArtistType.ARTIST)
                        .withName("Kosheen")
                        .build()
        );
        log.info("Created artist");
        Assertions.assertDoesNotThrow(() -> service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_OK).toString()));
        Assertions.assertEquals(ProcessingStatus.WARNING, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(10)
    @Sql({"/schema.sql", "/data.sql"})
    void testNoArtistResolving() {
        List<ProcessDetail> processDetail;

        service.executeProcessor(
                PROCESSOR_TYPE,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MP3_TEST_MP3_OK).toString());
        processDetail = service.getProcessInfo().getProcessDetails();
        Assertions.assertEquals(6, processDetail.size());
        Assertions.assertEquals(ProcessingStatus.WARNING, service.getProcessInfo().getProcessingStatus());

        // find first action
        ProcessingAction processingAction = processDetail.get(1).getProcessingAction();
        Assertions.assertNotNull(processingAction);

        // resolve first action
        service.getProcessInfo().resolveAction(processingAction);
        Assertions.assertEquals(5, processDetail.size());
        Assertions.assertEquals(ProcessingStatus.WARNING,
                service.getProcessInfo().getProcessDetails().getLast().getStatus());

        // find second action
        processingAction = processDetail.get(1).getProcessingAction();
        Assertions.assertNotNull(processingAction);

        // resolve second action
        service.getProcessInfo().resolveAction(processingAction);
        Assertions.assertEquals(4, processDetail.size());
    }
}
