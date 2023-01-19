package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
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

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIf(value = "${full.tests.disabled}", loadContext = true)
public class ServiceProcessLoadMusicMediaFilesDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadMusicMediaFilesDVTest.class.getSimpleName());

    @Autowired
    ProcessService service;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    AppConfiguration appConfiguration;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepare() {
        DbManagerService.loadOrPrepare(appConfiguration, DbManagerService.DbType.DB_ARTISTS_DV_MUSIC, () -> {
            service.executeProcessor(ProcessorType.ARTISTS_IMPORTER);
            Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
            log.info("Artist Importer Processing info: " + service.getProcessInfo());

            service.executeProcessor(ProcessorType.DV_MUSIC_IMPORTER);
            Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
            log.info("DV Music Importer Processing info: " + service.getProcessInfo());
        });
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testLoadMediaFiles() {
        int oldCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(ArtifactType.withDVMusic()).size();

        service.executeProcessor(ProcessorType.DV_MUSIC_MEDIA_LOADER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        log.info("Music Media Loader Processing info: " + service.getProcessInfo());

        int newCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(ArtifactType.withDVMusic()).size();

        Assertions.assertTrue(newCount < oldCount);
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testGetEmptyMediaFiles() {
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
    @Rollback(false)
    void testEmptyArtifacts() {
        List<Artifact> artifacts = artifactRepository.getAllByArtifactType(ArtifactType.withDVMusic());
        Assertions.assertTrue(artifacts.size() > 0);

        List<Artifact> emptyDurationArtifacts = artifacts
                .stream()
                .filter(a -> a.getDuration() == null || a.getDuration() == 0)
                .collect(Collectors.toList());
        Assertions.assertTrue(emptyDurationArtifacts.size() > 0);

        Assertions.assertTrue(emptyDurationArtifacts.size() < artifacts.size());
    }
}
