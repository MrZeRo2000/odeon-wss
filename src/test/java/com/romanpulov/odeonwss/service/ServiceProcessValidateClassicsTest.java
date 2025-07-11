package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.generator.DataGenerator;
import com.romanpulov.odeonwss.generator.FileTreeGenerator;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
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
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceProcessValidateClassicsTest {

    private static final Logger log = Logger.getLogger(ServiceProcessValidateClassicsTest.class.getName());

    @Autowired
    DataGenerator dataGenerator;

    @Value("${test.data.path}")
    String testDataPath;

    private enum TestFolder {
        TF_SERVICE_PROCESS_VALIDATE_CLASSICS_TEST_OK
    }

    private Map<TestFolder, Path> tempDirs;

    @BeforeAll
    public void setup() throws Exception {
        log.info("Before all");

        tempDirs = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_CLASSICS_TEST_OK),
                this.testDataPath,
                """
{
    "Бетховен - Сонаты - Горовиц": {
        "01.mp3": "sample_mp3_1.mp3",
        "02.mp3": "sample_mp3_1.mp3"
    },
    "Шопен - Вальсы и камерная музыка" : {
        "01.mp3": "sample_mp3_1.mp3",
        "02.mp3": "sample_mp3_1.mp3"
    },
    "Старинная музыка для органа и трубы" : {
        "01.mp3": "sample_mp3_1.mp3"
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

    @Autowired
    ProcessService service;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    TrackRepository trackRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testLoad() throws Exception {

        String json =
                """
{
    "artists": [
        {"artistType": "C", "artistName": "Шопен Фредерик"},
        {"artistType": "C", "artistName": "Бетховен"},
        {"artistType": "C", "artistName": "Various Artists"}
    ],
    "artifacts": [
        {"artifactType": { "id": 101 }, "artist": {"artistName": "Бетховен"}, "title": "Бетховен - Сонаты - Горовиц", "duration": 0 },
        {"artifactType": { "id": 102 }, "artist": {"artistName": "Шопен Фредерик"}, "title": "Шопен - Вальсы и камерная музыка", "duration": 0 },
        {"artifactType": { "id": 102 }, "artist": {"artistName": "Various Artists"}, "title": "Старинная музыка для органа и трубы", "duration": 0 }
    ],
    "tracks": [
        {"artifact": { "title": "Бетховен - Сонаты - Горовиц"}, "title": "Соната 1", "diskNum": 1, "num": 1},
        {"artifact": { "title": "Бетховен - Сонаты - Горовиц"}, "title": "Соната 2", "diskNum": 1, "num": 2},
        {"artifact": { "title": "Бетховен - Сонаты - Горовиц"}, "title": "Соната 3", "diskNum": 1, "num": 3},
        {"artifact": { "title": "Шопен - Вальсы и камерная музыка"}, "title": "3 вальса Op64", "diskNum": 1, "num": 1},
        {"artifact": { "title": "Шопен - Вальсы и камерная музыка"}, "title": "2 вальса Op69", "diskNum": 1, "num": 2},
        {"artifact": { "title": "Шопен - Вальсы и камерная музыка"}, "title": "Вальс в E-Dur", "diskNum": 1, "num": 4}
    ]
}
                """;

        dataGenerator.generateFromJSON(json);


        /*
        service.executeProcessor(ProcessorType.CLASSICS_IMPORTER);

        log.info("Processing info: " + service.getProcessInfo());
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

         */
    }

    @Test
    @Order(2)
    void testValidateFailed() {
        service.executeProcessor(
                ProcessorType.CLASSICS_VALIDATOR,
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_CLASSICS_TEST_OK).toString());
        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();

        log.info("Processing info: " + service.getProcessInfo());
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);

        int id = 0;
        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Classics validator"),
                        ProcessingStatus.INFO,
                        null,
                        null));

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts validated"),
                        ProcessingStatus.INFO,
                        null,
                        null));

        assertThat(processDetails.get(id++)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessageItems(
                                "Track numbers for artifact not increasing monotonically",
                                List.of("Шопен Фредерик >> Шопен - Вальсы и камерная музыка")),
                        ProcessingStatus.FAILURE,
                        null,
                        null));

        assertThat(processDetails.get(id)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.FAILURE,
                        null,
                        null)
        );

        Artifact errorArtifact = artifactRepository.findAll()
                .stream()
                .filter(a -> a.getTitle().equals("Шопен - Вальсы и камерная музыка"))
                .findFirst()
                .orElseThrow();

        trackRepository.deleteAll(trackRepository.findAllByArtifact(errorArtifact));

        service.executeProcessor(
                ProcessorType.CLASSICS_VALIDATOR,
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_CLASSICS_TEST_OK).toString());
        ProcessInfo pi2 = service.getProcessInfo();

        log.info("Processing info: " + service.getProcessInfo());
        assertThat(pi2.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
    }

    @Test
    @Order(3)
    void testFail() {
        Artifact artifact = artifactRepository.findById(1L).orElseThrow();
        artifact.setTitle(artifact.getTitle() + "(changed)");
        artifactRepository.save(artifact);

        service.executeProcessor(
                ProcessorType.CLASSICS_VALIDATOR,
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_VALIDATE_CLASSICS_TEST_OK).toString());

        log.info("Processing info: " + service.getProcessInfo());
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.FAILURE);
    }
}
