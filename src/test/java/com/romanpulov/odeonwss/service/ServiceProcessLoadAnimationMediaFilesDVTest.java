package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.generator.FileTreeGenerator;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessLoadAnimationMediaFilesDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadAnimationMediaFilesDVTest.class.getSimpleName());

    @Value("${test.data.path}")
    String testDataPath;

    private enum TestFolder {
        TF_SERVICE_PROCESS_LOAD_ANIMATION_MEDIA_FILES_DV_TEST_OK
    }

    private Map<TestFolder, Path> tempFolders;

    @BeforeAll
    public void setup() throws Exception {
        this.tempFolders = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_ANIMATION_MEDIA_FILES_DV_TEST_OK),
                this.testDataPath,
                """
                        {
                          "Talespin": {
                            "01 Plunder and Lightning (part-1).avi": "sample_AVI_480_750kB.avi",
                            "01 Plunder and Lightning (part-2).avi": "sample_AVI_480_750kB.avi",
                            "02 From Here to Machinery original.avi": "sample_AVI_480_750kB.avi",
                            "03 Louie’s Last Stand.avi": "sample_AVI_480_750kB.avi"
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
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    private ArtifactType artifactType;

    @BeforeEach
    void beforeEach() {
        this.artifactType = artifactTypeRepository.getWithDVAnimation();
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepare() {
        service.executeProcessor(
                ProcessorType.DV_ANIMATION_LOADER,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_ANIMATION_MEDIA_FILES_DV_TEST_OK).toString());
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        log.info("Animation loader Processing info: " + service.getProcessInfo());
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testLoadMediaFiles() {
        // set size = 0
        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(artifactType).getFirst();
        mediaFile.setSize(0L);
        mediaFileRepository.save(mediaFile);

        int oldCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(
                artifactType).size();
        assertThat(oldCount).isGreaterThan(0);

        service.executeProcessor(
                ProcessorType.DV_ANIMATION_MEDIA_LOADER,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_ANIMATION_MEDIA_FILES_DV_TEST_OK).toString());

        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        log.info("Animation Media Loader Processing info: " + service.getProcessInfo());

        int newCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(
                artifactType).size();
        assertThat(newCount).isEqualTo(0);
    }
}
