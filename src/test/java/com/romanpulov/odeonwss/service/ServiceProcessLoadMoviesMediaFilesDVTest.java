package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.DatabaseConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(value = "test-01")
public class ServiceProcessLoadMoviesMediaFilesDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadMoviesMediaFilesDVTest.class.getSimpleName());
    public static final String ARTIFACT_TITLE = "Лицензия на убийство";
    public static final String MEDIA_FILE_TITLE = "Licence to Kill (HD).m4v";

    @Autowired
    ProcessService service;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private DatabaseConfiguration databaseConfiguration;

    private ArtifactType getArtifactType() {
        return artifactTypeRepository.getWithDVMovies();
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepare() {
        DbManagerService.loadOrPrepare(databaseConfiguration, DbManagerService.DbType.DB_IMPORTED_MOVIES, () -> {
            service.executeProcessor(ProcessorType.DV_MOVIES_IMPORTER);
            assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
            log.info("Movies Importer Processing info: " + service.getProcessInfo());
        });
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testLoadMediaFiles() {
        int oldCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(getArtifactType()).size();

        service.executeProcessor(ProcessorType.DV_MOVIES_MEDIA_LOADER);
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        log.info("Movies Media Loader Processing info: " + service.getProcessInfo());

        int newCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(getArtifactType()).size();
        assertThat(newCount).isLessThan(oldCount);
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testGetEmptyMediaFiles() {
        List<MediaFile> mediaFiles = mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(getArtifactType());
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
        List<Artifact> artifacts = artifactRepository.getAllByArtifactType(getArtifactType());
        List<Artifact> emptyDurationArtifacts = artifacts
                .stream()
                .filter(a -> a.getDuration() != null && a.getDuration() == 0)
                .toList();
        assertThat(emptyDurationArtifacts.size()).isLessThan(artifacts.size());
    }

    @Test
    @Order(4)
    @Rollback(false)
    void testEmptyTracks() {
        List<Artifact> artifacts = artifactRepository.getAllByArtifactTypeWithTracks(getArtifactType());
        assertThat(artifacts.isEmpty()).isFalse();

        List<Artifact> emptyDurationArtifacts = artifacts
                .stream()
                .filter(a -> a.getDuration() != null && a.getDuration() == 0)
                .toList();

        List<Track> tracks = artifacts.stream().map(Artifact::getTracks).flatMap(List::stream).toList();
        assertThat(tracks.isEmpty()).isFalse();
        assertThat(artifacts.size()).isEqualTo(tracks.size());

        List<Track> emptyDurationTracks = tracks
                .stream()
                .filter(c -> c.getDuration() == null || c.getDuration() == 0)
                .toList();
        assertThat(emptyDurationTracks.size()).isLessThan(tracks.size());
        assertThat(emptyDurationArtifacts.size()).isEqualTo(emptyDurationTracks.size());
    }

    @Test
    @Order(5)
    @Rollback(false)
    void testChangedFileSize() {
        Artifact artifact = artifactRepository.getAllByArtifactType(getArtifactType())
                .stream()
                .filter(a -> a.getTitle().equals(ARTIFACT_TITLE))
                .findFirst()
                .orElseThrow();
        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(getArtifactType())
                .stream()
                .filter(m -> m.getName().equals(MEDIA_FILE_TITLE))
                .findFirst()
                .orElseThrow();

        long oldSize = mediaFile.getSize();
        assertThat(artifact.getSize()).isEqualTo(oldSize);

        // change file name
        long newSize = oldSize + 1000L;
        mediaFile.setSize(newSize);
        mediaFileRepository.save(mediaFile);

        // check sizes: should be updated
        MediaFile updatedMediaFile = mediaFileRepository.getMediaFilesByArtifactType(getArtifactType())
                .stream()
                .filter(m -> m.getName().equals(MEDIA_FILE_TITLE))
                .findFirst()
                .orElseThrow();
        assertThat(updatedMediaFile.getSize()).isEqualTo(newSize);

        // run processor
        service.executeProcessor(ProcessorType.DV_MOVIES_MEDIA_LOADER);
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        // check sizes: should be updated
        Artifact processedArtifact = artifactRepository.getAllByArtifactType(getArtifactType())
                .stream()
                .filter(a -> a.getTitle().equals(ARTIFACT_TITLE))
                .findFirst()
                .orElseThrow();
        MediaFile processedMediaFile = mediaFileRepository.getMediaFilesByArtifactType(getArtifactType())
                .stream()
                .filter(m -> m.getName().equals(MEDIA_FILE_TITLE))
                .findFirst()
                .orElseThrow();

        assertThat(processedMediaFile.getSize()).isEqualTo(oldSize);
        assertThat(processedArtifact.getSize()).isEqualTo(oldSize);
    }
}
