package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessLoadAnimationMediaFilesDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadAnimationMediaFilesDVTest.class.getSimpleName());

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
        service.executeProcessor(ProcessorType.DV_ANIMATION_LOADER);
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        log.info("Animation loader Processing info: " + service.getProcessInfo());
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testLoadMediaFiles() {
        // set size = 0
        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(artifactType).get(0);
        mediaFile.setSize(0L);
        mediaFileRepository.save(mediaFile);

        int oldCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(
                artifactType).size();
        assertThat(oldCount).isGreaterThan(0);

        service.executeProcessor(ProcessorType.DV_ANIMATION_MEDIA_LOADER);
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        log.info("Animation Media Loader Processing info: " + service.getProcessInfo());

        int newCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(
                artifactType).size();
        assertThat(newCount).isEqualTo(0);
    }
}
