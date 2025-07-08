package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.generator.DataGenerator;
import com.romanpulov.odeonwss.generator.FileTreeGenerator;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ActiveProfiles(value = "test-01")
public class ServiceProcessLoadMoviesMediaFilesDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadMoviesMediaFilesDVTest.class.getSimpleName());

    @Value("${test.data.path}")
    String testDataPath;

    private enum TestFolder {
        TF_SERVICE_PROCESS_LOAD_MOVIES_MEDIA_FILES_DV_TEST
    }

    private Map<TestFolder, Path> tempFolders;

    @BeforeAll
    public void setup() throws Exception {
        this.tempFolders = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MOVIES_MEDIA_FILES_DV_TEST),
                this.testDataPath,
                """
{
  "Коломбо": {
    "01 Рецепт убийства.MKV": "sample_960x540_capital_ext.MKV",
    "02 Убийство по книге.m4v": "sample_960x540.m4v"
  },
  "Крепкий орешек": {
    "Die.Hard.1988.BDRip.720p.stimtoo.mkv": "sample_1280x720_with_chapters.mkv",
    "readme.txt": "sample.txt"
  },
  "Лицензия на убийство": {
    "Licence to Kill (HD).m4v": "sample_960x540.m4v"
  },
  "Обыкновенное чудо": {
    "Part 1.avi": "sample_AVI_480_750kB.avi",
    "Part 2.avi": "sample_AVI_480_750kB.avi"
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
    DataGenerator dataGenerator;

    @Autowired
    ProcessService service;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    private ArtifactType getArtifactType() {
        return artifactTypeRepository.getWithDVMovies();
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepare() throws Exception {
        String json =
                """
                {
                    "artifacts": [
                        {"artifactType": { "id": 202 }, "title": "10 ярдов", "duration": 0 },
                        {"artifactType": { "id": 202 }, "title": "Крепкий орешек", "duration": 0 },
                        {"artifactType": { "id": 202 }, "title": "Goldfinger", "duration": 0 }
                    ],
                    "mediaFiles": [
                        {"artifactTitle": "10 ярдов", "name": "10 yards part 1.mkv" },
                        {"artifactTitle": "10 ярдов", "name": "10 yards part 2.mkv" },
                        {"artifactTitle": "Крепкий орешек", "name": "Die.Hard.1988.BDRip.720p.stimtoo.mkv"},
                        {"artifactTitle": "Goldfinger", "name": "goldfinger.mkv"}
                    ],
                    "tracks": [
                        {"artifact": { "title": "10 ярдов"}, "title": "10 ярдов", "mediaFiles": [
                                {"name": "10 yards part 1.mkv"},
                                {"name": "10 yards part 2.mkv"}
                            ]
                        },
                        { "artifact": {"title": "Крепкий орешек"}, "title": "Крепкий орешек", "mediaFiles": [
                                {"name": "Die.Hard.1988.BDRip.720p.stimtoo.mkv"}
                            ]
                        },
                        { "artifact": {"title": "Goldfinger"}, "title": "Goldfinger", "mediaFiles": [
                                {"name": "goldfinger.mkv"}
                            ]
                        }
                    ]
                }
                """;

        dataGenerator.generateFromJSON(json);

        service.executeProcessor(
                ProcessorType.DV_MOVIES_LOADER,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MOVIES_MEDIA_FILES_DV_TEST).toString());
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        log.info("Movies Loader Processing info: " + service.getProcessInfo());

        /*
        DbManagerService.loadOrPrepare(databaseConfiguration, DbManagerService.DbType.DB_IMPORTED_MOVIES, () -> {
            service.executeProcessor(ProcessorType.DV_MOVIES_IMPORTER);
            assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
            log.info("Movies Importer Processing info: " + service.getProcessInfo());

            service.executeProcessor(ProcessorType.DV_MOVIES_LOADER);
            assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
            log.info("Movies Loader Processing info: " + service.getProcessInfo());
        });

         */
    }

    @Test
    @Order(2)
    @Rollback(false)
    void testLoadMediaFiles() {
        int oldCount =  mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(getArtifactType()).size();

        service.executeProcessor(
                ProcessorType.DV_MOVIES_MEDIA_LOADER,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MOVIES_MEDIA_FILES_DV_TEST).toString());
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
        assertThat(artifacts.size()).isLessThanOrEqualTo(tracks.size());

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
                .filter(a -> a.getTitle().equals("Коломбо"))
                .findFirst()
                .orElseThrow();
        MediaFile mediaFile = mediaFileRepository.getMediaFilesByArtifactType(getArtifactType())
                .stream()
                .filter(m -> m.getName().equals("01 Рецепт убийства.MKV"))
                .findFirst()
                .orElseThrow();

        long oldMediaFileSize = mediaFile.getSize();
        long oldArtifactSize = Optional.ofNullable(artifact.getSize()).orElseThrow();

        // change file size
        long newSize = oldMediaFileSize + 1000L;
        mediaFile.setSize(newSize);
        mediaFileRepository.save(mediaFile);

        // check sizes: should be updated
        MediaFile updatedMediaFile = mediaFileRepository.getMediaFilesByArtifactType(getArtifactType())
                .stream()
                .filter(m -> m.getName().equals("01 Рецепт убийства.MKV"))
                .findFirst()
                .orElseThrow();
        assertThat(updatedMediaFile.getSize()).isEqualTo(newSize);

        // run processor
        service.executeProcessor(
                ProcessorType.DV_MOVIES_MEDIA_LOADER,
                tempFolders.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MOVIES_MEDIA_FILES_DV_TEST).toString());
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        // check sizes: should be updated
        Artifact processedArtifact = artifactRepository.getAllByArtifactType(getArtifactType())
                .stream()
                .filter(a -> a.getTitle().equals("Коломбо"))
                .findFirst()
                .orElseThrow();
        MediaFile processedMediaFile = mediaFileRepository.getMediaFilesByArtifactType(getArtifactType())
                .stream()
                .filter(m -> m.getName().equals("01 Рецепт убийства.MKV"))
                .findFirst()
                .orElseThrow();

        assertThat(processedMediaFile.getSize()).isEqualTo(oldMediaFileSize);
        assertThat(processedArtifact.getSize()).isEqualTo(oldArtifactSize);
    }
}
