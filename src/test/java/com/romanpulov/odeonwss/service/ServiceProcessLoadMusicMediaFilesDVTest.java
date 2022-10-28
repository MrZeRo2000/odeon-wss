package com.romanpulov.odeonwss.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessLoadMusicMediaFilesDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadMusicMediaFilesDVTest.class.getSimpleName());

    @Autowired
    ProcessService service;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    // @Disabled("For dev purposes")
    void testPrepare() {
        service.executeProcessor(ProcessorType.ARTISTS_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        log.info("Artist Importer Processing info: " + service.getProcessInfo());

        service.executeProcessor(ProcessorType.DV_MUSIC_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        log.info("DV Music Importer Processing info: " + service.getProcessInfo());
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testLoadMediaFiles() throws Exception {
        int oldCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(ArtifactType.withDVMusic()).size();

        service.executeProcessor(ProcessorType.DV_MUSIC_MEDIA_LOADER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        log.info("Music Media Loader Processing info: " + service.getProcessInfo());

        int newCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(ArtifactType.withDVMusic()).size();

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
}
