package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.DatabaseConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
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
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private DatabaseConfiguration databaseConfiguration;;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepare() throws Exception {
        DbManagerService.loadOrPrepare(databaseConfiguration, DbManagerService.DbType.DB_IMPORTED_MOVIES, () -> {
            service.executeProcessor(ProcessorType.DV_MOVIES_IMPORTER);
            Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
            log.info("Movies Importer Processing info: " + service.getProcessInfo());
        });
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testLoadMediaFiles() {
        int oldCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(artifactTypeRepository.getWithDVMovies()).size();

        service.executeProcessor(ProcessorType.DV_MOVIES_MEDIA_LOADER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        log.info("Movies Media Loader Processing info: " + service.getProcessInfo());

        int newCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(artifactTypeRepository.getWithDVMovies()).size();

        Assertions.assertTrue(newCount < oldCount);
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testGetEmptyMediaFiles() {
        List<MediaFile> mediaFiles = mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(artifactTypeRepository.getWithDVMusic());
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
        List<Artifact> artifacts = artifactRepository.getAllByArtifactType(artifactTypeRepository.getWithDVMovies());
        List<Artifact> emptyDurationArtifacts = artifacts
                .stream()
                .filter(a -> a.getDuration() != null && a.getDuration() == 0)
                .collect(Collectors.toList());
        Assertions.assertTrue(emptyDurationArtifacts.size() < artifacts.size());
    }

    @Test
    @Order(4)
    @Rollback(false)
    void testEmptyTracks() {
        List<Artifact> artifacts = artifactRepository.getAllByArtifactTypeWithTracks(artifactTypeRepository.getWithDVMovies());
        Assertions.assertTrue(artifacts.size() > 0);

        List<Artifact> emptyDurationArtifacts = artifacts
                .stream()
                .filter(a -> a.getDuration() != null && a.getDuration() == 0)
                .collect(Collectors.toList());

        List<Track> tracks = artifacts.stream().map(Artifact::getTracks).flatMap(List::stream).collect(Collectors.toList());
        Assertions.assertTrue(tracks.size() > 0);
        Assertions.assertEquals(artifacts.size(), tracks.size());

        List<Track> emptyDurationTracks = tracks
                .stream()
                .filter(c -> c.getDuration() == null || c.getDuration() == 0)
                .collect(Collectors.toList());
        Assertions.assertTrue(emptyDurationTracks.size() < tracks.size());
        Assertions.assertEquals(emptyDurationArtifacts.size(), emptyDurationTracks.size());
    }
}
