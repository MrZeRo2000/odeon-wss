package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.MediaFile;
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
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceProcessLoadMusicMediaFilesDVTest {
    private static final Logger log = Logger.getLogger(ServiceProcessLoadMusicMediaFilesDVTest.class.getSimpleName());

    @Value("${test.data.path}")
    String testDataPath;

    @Autowired
    DataGenerator dataGenerator;

    private enum TestFolder {
        TF_SERVICE_PROCESS_LOAD_MUSIC_MEDIA_FILES_DV_TEST_OK
    }

    private Map<TestFolder, Path> tempDirs;

    @BeforeAll
    public void setup() throws Exception {
        log.info("Before all");

        tempDirs = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_MEDIA_FILES_DV_TEST_OK),
                this.testDataPath,
                """
{
  "Beautiful Voices 1": {
    "Beautiful Voices 1.mkv": "sample_1280x720_with_chapters.mkv",
    "tracks.txt": "sample.txt"
  },
  "The Cure - Picture Show 1991": {
    "The Cure - Picture Show 1991.mp4": "sample_MP4_480_1_5MG.mp4"
  },
  "Tori Amos - Fade to Red 2006": {
    "Tori Amos - Fade to Red Disk 1 2006.mkv": "sample_1280x720_with_chapters.mkv",
    "Tori Amos - Fade to Red Disk 2 2006.mkv": "sample_1280x720_600.mkv"
  }
}
                """
        );

    }

    @Autowired
    ProcessService service;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    @Rollback(false)
    void testPrepare() throws Exception {
        String json =
                """
{
    "artists": [
        {"artistType": "A", "artistName": "The Cure"},
        {"artistType": "A", "artistName": "Tori Amos"},
        {"artistType": "A", "artistName": "Black Sabbath"},
        {"artistType": "A", "artistName": "Therapy"}
    ],
    "artifacts": [
        {"artifactType": { "id": 201 }, "artist": {"artistName": "The Cure"}, "title": "The Cure - Picture Show 1991", "duration": 0 },
        {"artifactType": { "id": 201 }, "artist": {"artistName": "Tori Amos"}, "title": "Tori Amos - Fade To Red", "duration": 0 },
        {"artifactType": { "id": 201 }, "artist": {"artistName": "Black Sabbath"}, "title": "Black Sabbath - VideoStory 2010", "duration": 0 },
        {"artifactType": { "id": 201 }, "artist": {"artistName": "Therapy"}, "title": "Therapy - Scopophobia 2003", "duration": 0 }
    ],
    "mediaFiles": [
        {"artifactTitle": "The Cure - Picture Show 1991", "name": "The Cure - Picture Show 1991.mp4" },
        {"artifactTitle": "Tori Amos - Fade To Red", "name": "Tori Amos - Fade to Red Disk 1 2006.mkv" },
        {"artifactTitle": "Tori Amos - Fade To Red", "name": "Tori Amos - Fade to Red Disk 2 2006.mkv" },
        {"artifactTitle": "Black Sabbath - VideoStory 2010", "name": "Black Sabbath - VideoStory 2010.mkv"},
        {"artifactTitle": "Therapy - Scopophobia 2003", "name": "therapy.mkv"}
    ],
    "tracks": [
        {"artifact": { "title": "Tori Amos - Fade To Red"}, "title": "Tori Amos - Fade To Red 1", "mediaFiles": [
                {"name": "Tori Amos - Fade to Red Disk 1 2006.mkv"}
            ]
        },
        {"artifact": { "title": "Tori Amos - Fade To Red"}, "title": "Tori Amos - Fade To Red 2", "mediaFiles": [
                {"name": "Tori Amos - Fade to Red Disk 2 2006.mkv"}
            ]
        },
        { "artifact": {"title": "The Cure - Picture Show 1991"}, "title": "The Cure - Picture Show 1991", "mediaFiles": [
                {"name": "The Cure - Picture Show 1991.mp4"}
            ]
        },
        { "artifact": {"title": "Black Sabbath - VideoStory 2010"}, "title": "Black Sabbath - VideoStory 2010", "mediaFiles": [
                {"name": "Black Sabbath - VideoStory 2010.mkv"}
            ]
        }
    ]
}
                """;

        dataGenerator.generateFromJSON(json);
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

        service.executeProcessor(
                ProcessorType.DV_MUSIC_MEDIA_LOADER,
                tempDirs.get(TestFolder.TF_SERVICE_PROCESS_LOAD_MUSIC_MEDIA_FILES_DV_TEST_OK).toString());
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
        assertThat(ps.getSize()).isEqualTo(1570024L);
        assertThat(ps.getDuration()).isEqualTo(31);
        assertThat(ps.getBitrate()).isEqualTo(300L);
        assertThat(ps.getWidth()).isEqualTo(480L);
        assertThat(ps.getHeight()).isEqualTo(270L);
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
