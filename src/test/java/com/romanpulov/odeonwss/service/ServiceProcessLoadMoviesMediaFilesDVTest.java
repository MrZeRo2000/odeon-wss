package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIf(value = "${full.tests.disabled}", loadContext = true)
public class ServiceProcessLoadMoviesMediaFilesDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadMoviesMediaFilesDVTest.class.getSimpleName());

    @Autowired
    ProcessService service;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @PersistenceContext
    EntityManager em;
    @Autowired
    private ArtifactRepository artifactRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepare() {
        service.executeProcessor(ProcessorType.DV_MOVIES_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        log.info("Movies Importer Processing info: " + service.getProcessInfo());

    }

    @Test
    @Order(2)
    @Rollback(false)
    void testLoadMediaFiles() throws Exception {
        int oldCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(ArtifactType.withDVMovies()).size();

        service.executeProcessor(ProcessorType.DV_MOVIES_MEDIA_LOADER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        log.info("Movies Media Loader Processing info: " + service.getProcessInfo());

        int newCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(ArtifactType.withDVMovies()).size();

        Assertions.assertTrue(newCount < oldCount);
    }

    @Test
    @Order(2)
    void testGetEmptyMediaFiles() throws Exception {
        List<MediaFile> mediaFiles = mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(ArtifactType.withDVMusic());
        log.info("Artifacts:" + mediaFiles.stream().map(v -> v.getArtifact().getTitle()).collect(Collectors.toList()));
        for (MediaFile mediaFile: mediaFiles) {
            MediaFile getMediaFile = mediaFileRepository.findById(mediaFile.getId()).orElseThrow();
            log.info("Artifact title:" + mediaFile.getArtifact().getTitle());
            log.info("MediaFile:" + getMediaFile);
        }
    }

    @Test
    @Order(3)
    void testEmptyArtifacts() throws Exception {
        List<Artifact> artifacts = artifactRepository.getAllByArtifactType(ArtifactType.withDVMovies());
        List<Artifact> emptyDurationArtifacts = artifacts
                .stream()
                .filter(a -> a.getDuration() != null && a.getDuration() == 0)
                .collect(Collectors.toList());
        Assertions.assertTrue(emptyDurationArtifacts.size() < artifacts.size());
    }
}
