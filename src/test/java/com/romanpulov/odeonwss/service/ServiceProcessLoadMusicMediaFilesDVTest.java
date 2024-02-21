package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.DatabaseConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(value = "test-03")
public class ServiceProcessLoadMusicMediaFilesDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadMusicMediaFilesDVTest.class.getSimpleName());

    @Autowired
    ProcessService service;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    DatabaseConfiguration databaseConfiguration;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepare() {
        DbManagerService.loadOrPrepare(databaseConfiguration, DbManagerService.DbType.DB_ARTISTS_DV_MUSIC_IMPORT, () -> {
            DbManagerService.loadOrPrepare(databaseConfiguration, DbManagerService.DbType.DB_IMPORTED_ARTISTS, () -> {
                service.executeProcessor(ProcessorType.ARTISTS_IMPORTER);
                assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
                log.info("Artist Importer Processing info: " + service.getProcessInfo());
            });

            service.executeProcessor(ProcessorType.DV_MUSIC_IMPORTER);
            assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
            log.info("DV Music Importer Processing info: " + service.getProcessInfo());
        });
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testLoadMediaFiles() {
        var oldIds = mediaFileRepository
                .getMediaFilesWithEmptySizeByArtifactType(artifactTypeRepository.getWithDVMusic())
                .stream()
                .map(MediaFile::getId)
                .collect(Collectors.toSet());

        service.executeProcessor(ProcessorType.DV_MUSIC_MEDIA_LOADER);
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        log.info("Music Media Loader Processing info: " + service.getProcessInfo());

        var newIds = mediaFileRepository
                .getMediaFilesWithEmptySizeByArtifactType(artifactTypeRepository.getWithDVMusic())
                .stream()
                .map(MediaFile::getId)
                .collect(Collectors.toSet());

        assertThat(newIds.size() < oldIds.size()).isTrue();

        var changedIds = oldIds
                .stream()
                .filter(v -> !newIds.contains(v))
                .toList();
        assertThat(changedIds.isEmpty()).isFalse();
        log.info("Changed Ids:" + changedIds);

        var changedMediaFiles = StreamSupport
                .stream(mediaFileRepository.findAllById(changedIds).spliterator(), false)
                .toList();
        var ps = changedMediaFiles
                .stream()
                .filter(m -> m.getName().equals("The Cure - Picture Show 1991.mp4"))
                .findFirst()
                .orElseThrow();
        assertThat(ps.getSize()).isEqualTo(44863665L);
        assertThat(ps.getDuration()).isEqualTo(3 * 60 + 36);
        assertThat(ps.getBitrate()).isEqualTo(1522L);
        assertThat(ps.getWidth()).isEqualTo(1280L);
        assertThat(ps.getHeight()).isEqualTo(720L);
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testGetEmptyMediaFiles() {
        List<MediaFile> mediaFiles =
                mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(artifactTypeRepository.getWithDVMusic());
        log.info("Artifacts:" + mediaFiles.stream().map(v -> v.getArtifact().getTitle()).toList());
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
        List<Artifact> artifacts = artifactRepository.getAllByArtifactType(artifactTypeRepository.getWithDVMusic());
        assertThat(artifacts.isEmpty()).isFalse();

        List<Artifact> emptyDurationArtifacts = artifacts
                .stream()
                .filter(a -> a.getDuration() == null || a.getDuration() == 0)
                .toList();
        assertThat(!emptyDurationArtifacts.isEmpty()).isTrue();

        assertThat(emptyDurationArtifacts.size() < artifacts.size()).isTrue();
    }
}
