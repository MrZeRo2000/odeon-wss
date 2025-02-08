package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import com.romanpulov.odeonwss.generator.FileTreeGenerator;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.jdbc.Sql;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceMediaFileTest {
    private static final Logger log = Logger.getLogger(ServiceMediaFileTest.class.getSimpleName());

    @Value("${test.data.path}")
    String testDataPath;

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private MediaFileService service;

    @Autowired
    public ArtifactTypeService artifactTypeService;

    private enum TestFolder {
        TF_ONE_VIDEO_FILE,
        TF_ONE_MP3_FOLDER
    }

    static class TestArtifactTypeService extends ArtifactTypeService {
        private String artifactTypePath;

        public TestArtifactTypeService(AppConfiguration appConfiguration, ArtifactTypeRepository artifactTypeRepository) {
            super(appConfiguration, artifactTypeRepository);
        }

        public String getArtifactTypePath(long artifactTypeId) {
            return artifactTypePath;
        }

        public void setArtifactTypePath(String artifactTypePath) {
            this.artifactTypePath = artifactTypePath;
        }
    }

    @TestConfiguration
    static class ArtifactTypeServiceConfig {
        @Bean
        @Primary
        ArtifactTypeService getArtifactTypeService(AppConfiguration appConfiguration, ArtifactTypeRepository artifactTypeRepository) {
            return new TestArtifactTypeService(appConfiguration, artifactTypeRepository);
        }
    }

    private Map<TestFolder, Path> tempDirs;

    @BeforeAll
    public void setup() throws Exception {
        log.info("Before all");

        tempDirs = FileTreeGenerator.createTempFolders(TestFolder.class);

        FileTreeGenerator.generateFromJSON(
                tempDirs.get(TestFolder.TF_ONE_VIDEO_FILE),
                this.testDataPath,
                """
                            {
                                "Scary Movie": {
                                    "Scary Movie Part 1.mkv": "files/sample_1280x720_with_chapters.mkv",
                                    "Scary Movie Part 2.mkv": "files/sample_1280x720_with_chapters.mkv",
                                    "Scary Movie Part 3.mkv": "files/sample_1280x720_with_chapters.mkv"
                                }
                            }
                        """
        );

        FileTreeGenerator.generateFromJSON(
                tempDirs.get(TestFolder.TF_ONE_MP3_FOLDER),
                this.testDataPath,
                """
                            {
                                "Aerosmith": {
                                    "2004 Honkin'On Bobo": {
                                        "01 - Road Runner.mp3": "files/01 - Lost.mp3"
                                    }
                                }
                            }
                        """
        );

        if (artifactTypeService instanceof TestArtifactTypeService) {
            ((TestArtifactTypeService) artifactTypeService).setArtifactTypePath(
                    tempDirs.get(TestFolder.TF_ONE_VIDEO_FILE).toString());
        }

    }

    @AfterAll
    public void teardown() {
        log.info("After all");
        FileTreeGenerator.deleteTempFiles(tempDirs.values());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @Order(1)
    void testPrepare() {
        artistRepository.save(
                new EntityArtistBuilder()
                        .withType(ArtistType.ARTIST)
                        .withName("Aerosmith")
                .build()
        );

        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtifactType(artifactTypeRepository.getWithDVMovies())
                        .withTitle("Scary Movie")
                        .build()
        );

        assertThat(artifactRepository.findById(1L)).isPresent();
        assertThat(artifactRepository.findAll().size()).isEqualTo(1);

        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtifactType(artifactTypeRepository.getWithMP3())
                        .withArtist(artistRepository.findById(1L).orElseThrow())
                        .withTitle("Honkin'On Bobo")
                        .withYear(2004L)
                .build()
        );

        assertThat(artifactRepository.findById(2L)).isPresent();
        assertThat(artifactRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @Order(2)
    void testGetMediaFileAttributes() throws Exception {
        var dto = service.getMediaFileAttributes(1L, "Scary Movie Part 1.mkv");
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("Scary Movie Part 1.mkv");
        assertThat(dto.getSize()).isNotNull();
        assertThat(dto.getBitrate()).isNotNull();
        assertThat(dto.getDuration()).isNotNull();
        assertThat(dto.getFormat()).isEqualTo("MKV");
        assertThat(dto.getWidth()).isEqualTo(1280L);
        assertThat(dto.getHeight()).isEqualTo(720L);
    }

    @Test
    @Order(3)
    void testInsertToEmptyMediaFiles() throws Exception {
        mediaFileRepository.deleteAll();

        assertThat(StreamSupport
                        .stream(mediaFileRepository
                        .findAll().spliterator(), false)
                        .count()
        ).isEqualTo(0);

        assertThat(service
                        .insertMediaFiles(1L, List.of("Scary Movie Part 1.mkv", "Scary Movie Part 2.mkv"))
                        .getRowsAffected()
        ).isEqualTo(2L);

        var mediaFiles = StreamSupport
                .stream(mediaFileRepository.findAll().spliterator(), false)
                .sorted(Comparator.comparing(MediaFile::getName))
                .toList();
        assertThat(mediaFiles).hasSize(2);
        assertThat(mediaFiles.get(0).getArtifact().getId()).isEqualTo(1);
        assertThat(mediaFiles.get(0).getName()).isEqualTo("Scary Movie Part 1.mkv");
        assertThat(mediaFiles.get(0).getFormat()).isEqualTo("MKV");
        assertThat(mediaFiles.get(0).getSize()).isEqualTo(17433330L);
        assertThat(mediaFiles.get(0).getBitrate()).isEqualTo(4841);
        assertThat(mediaFiles.get(0).getDuration()).isEqualTo(28);
        assertThat(mediaFiles.get(0).getWidth()).isEqualTo(1280);
        assertThat(mediaFiles.get(0).getHeight()).isEqualTo(720);
        assertThat(mediaFiles.get(0).getExtra()).isNotNull();

        assertThatThrownBy(() -> service.insertMediaFiles(1L, List.of("Scary Movie Part 1.mkv")))
                .isInstanceOf(WrongParameterValueException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @Order(4)
    void testGetMediaFiles() throws Exception {
        if (artifactTypeService instanceof TestArtifactTypeService) {
            ((TestArtifactTypeService) artifactTypeService).setArtifactTypePath(
                    tempDirs.get(TestFolder.TF_ONE_MP3_FOLDER).toString());
        }

        var mediaFiles = service.getMediaFiles(2L);
        assertThat(mediaFiles.size()).isEqualTo(1);
    }
}
