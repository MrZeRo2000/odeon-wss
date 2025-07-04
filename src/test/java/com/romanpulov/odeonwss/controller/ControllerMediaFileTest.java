package com.romanpulov.odeonwss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.odeonwss.builder.dtobuilder.MediaFileDTOBuilder;
import com.romanpulov.odeonwss.builder.dtobuilder.TrackDTOBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.config.ProjectConfigurationProperties;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.generator.FileTreeGenerator;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import jakarta.servlet.ServletContext;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerMediaFileTest {
    final static Logger logger = LoggerFactory.getLogger(ControllerMediaFileTest.class);

    @Autowired
    private MockMvc mockMvc;

    private enum TestFolder {
        TF_CONTROLLER_MEDIA_FILE_TEST_DV_MUSIC
    }

    private static final Map<TestFolder, Path> TEMP_FOLDERS = FileTreeGenerator.createTempFolders(TestFolder.class);

    static class TestAppConfiguration extends AppConfiguration {
        public TestAppConfiguration(ServletContext context, ProjectConfigurationProperties projectConfigurationProperties) {
            super(context, projectConfigurationProperties);
            this.pathMap.put(PathType.PT_DV_MUSIC, TEMP_FOLDERS.get(TestFolder.TF_CONTROLLER_MEDIA_FILE_TEST_DV_MUSIC).toString());
        }
    }

    @TestConfiguration
    static class TestAppConfigurationConfig {
        @Bean
        @Primary
        AppConfiguration getAppConfiguration(ServletContext context, ProjectConfigurationProperties projectConfigurationProperties) {
            return new TestAppConfiguration(context, projectConfigurationProperties);
        }
    }

    @Value("${test.data.path}")
    String testDataPath;

    @BeforeAll
    public void setup() throws Exception {
        FileTreeGenerator.generateFromJSON(
                TEMP_FOLDERS.get(TestFolder.TF_CONTROLLER_MEDIA_FILE_TEST_DV_MUSIC),
                this.testDataPath,
                """
                            {
                                "Tori Amos - Fade to Red 2006": {
                                    "Tori Amos - Fade to Red Disk 1 2006.mkv": "sample_1280x720_600.mkv",
                                    "Tori Amos - Fade to Red Disk 2 2006.mkv": "sample_1280x720_with_chapters.mkv"
                                }
                            }
                        """
        );
    }

    @AfterAll
    public void teardown() {
        FileTreeGenerator.deleteTempFiles(TEMP_FOLDERS.values());
    }

    @Autowired
    private ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void prepareDataShouldBeOk() throws Exception {
        Artist artist1 = artistRepository.save(
                new EntityArtistBuilder()
                        .withType(ArtistType.ARTIST)
                        .withName("Artist 1")
                        .build()
        );

        Artifact artifact1 = artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist1)
                        .withArtifactType(artifactTypeRepository.getWithLA())
                        .withTitle("Title 1")
                        .withYear(2001L)
                        .withDuration(12345L)
                        .build()
        );

        Artifact artifact2 = artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist1)
                        .withArtifactType(artifactTypeRepository.getWithLA())
                        .withTitle("Title 2")
                        .withYear(2001L)
                        .withDuration(4234L)
                        .build()
        );

        Artifact artifact3 = artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist1)
                        .withArtifactType(artifactTypeRepository.getWithDVMovies())
                        .withTitle("Movie 2")
                        .withDuration(54983L)
                        .build()
        );

        String json11 = mapper.writeValueAsString(
                new MediaFileDTOBuilder()
                        .withArtifactId(artifact1.getId())
                        .withName("Name 11")
                        .withFormat("ape")
                        .withBitrate(1000L)
                        .withSize(3423L)
                        .withDuration(52345L)
                        .build()
        );

        var result11 = this.mockMvc.perform(
                        post("/api/media-file").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json11)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("result 11:" + result11.getResponse().getContentAsString());

        String json111 = mapper.writeValueAsString(
                new TrackDTOBuilder()
                        .withArtifactId(artifact1.getId())
                        .withTitle("Track title 11")
                        .withDiskNum(1L)
                        .withNum(8L)
                        .withDuration(52345L)
                        .withMediaFileIds(List.of(1L))
                        .build()
        );

        var result111 = this.mockMvc.perform(
                        post("/api/track").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json111)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("result 111:" + result111.getResponse().getContentAsString());

        String json12 = mapper.writeValueAsString(
                new MediaFileDTOBuilder()
                        .withArtifactId(artifact1.getId())
                        .withName("Name 12")
                        .withFormat("ape")
                        .withBitrate(1001L)
                        .withSize(3424L)
                        .build()
        );

        var result12 = this.mockMvc.perform(
                        post("/api/media-file").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json12)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("result 12:" + result12.getResponse().getContentAsString());

        String json21 = mapper.writeValueAsString(
                new MediaFileDTOBuilder()
                        .withArtifactId(artifact2.getId())
                        .withName("Name 21")
                        .withFormat("ape")
                        .withBitrate(1004L)
                        .withSize(3438L)
                        .withDuration(57428L)
                        .build()
        );

        var result21 = this.mockMvc.perform(
                        post("/api/media-file").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json21)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("result 21:" + result21.getResponse().getContentAsString());

        String json31 = mapper.writeValueAsString(
                new MediaFileDTOBuilder()
                        .withArtifactId(artifact3.getId())
                        .withName("Video name 3")
                        .withFormat("MKV")
                        .withBitrate(2500L)
                        .withSize(59872L)
                        .withDuration(57428L)
                        .withDimensions(1280, 720)
                        .withExtra("{\"extra\": [\"00:01:53\", \"01:34:06\"]}")
                        .build()
        );

        var result31 = this.mockMvc.perform(
                        post("/api/media-file").accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json31)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("result 31:" + result31.getResponse().getContentAsString());

        Artist artist10 = artistRepository.save(
                new EntityArtistBuilder()
                        .withType(ArtistType.ARTIST)
                        .withName("Tori Amos")
                        .build()
        );

        artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtist(artist10)
                        .withArtifactType(artifactTypeRepository.getWithDVMusic())
                        .withTitle("Tori Amos - Fade to Red 2006")
                        .withYear(2002L)
                        .withDuration(12345L)
                        .build()
        );
    }

    @Test
    @Order(2)
    void testGetTableArtifact1() throws Exception {
        var result = this.mockMvc.perform(get("/api/media-file/table/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(6)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo("Name 11")))
                .andExpect(jsonPath("$[0].format", Matchers.equalTo("ape")))
                .andExpect(jsonPath("$[0].bitrate", Matchers.equalTo(1000)))
                .andExpect(jsonPath("$[0].duration", Matchers.equalTo(52345)))
                .andExpect(jsonPath("$[0].size", Matchers.equalTo(3423)))
                .andExpect(jsonPath("$[1]", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[1].duration").doesNotExist())
                .andReturn()
        ;
        logger.debug("testGetTableArtifact1:" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void testGetTableTrack1() throws Exception {
        var result = this.mockMvc.perform(get("/api/media-file/table?trackId=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(6)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo("Name 11")))
                .andExpect(jsonPath("$[0].format", Matchers.equalTo("ape")))
                .andExpect(jsonPath("$[0].bitrate", Matchers.equalTo(1000)))
                .andExpect(jsonPath("$[0].duration", Matchers.equalTo(52345)))
                .andExpect(jsonPath("$[0].size", Matchers.equalTo(3423)))
                .andReturn()
        ;
        logger.debug("testGetTableTrack1:" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void testGetTableArtifact2() throws Exception {
        this.mockMvc.perform(get("/api/media-file/table/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].size").exists())
        ;
    }


    @Test
    @Order(2)
    void testGetTableArtifact3() throws Exception {
        var result = this.mockMvc.perform(get("/api/media-file/table/3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(9)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(4)))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo("Video name 3")))
                .andExpect(jsonPath("$[0].format", Matchers.equalTo("MKV")))
                .andExpect(jsonPath("$[0].bitrate", Matchers.equalTo(2500)))
                .andExpect(jsonPath("$[0].duration", Matchers.equalTo(57428)))
                .andExpect(jsonPath("$[0].size", Matchers.equalTo(59872)))
                .andExpect(jsonPath("$[0].width", Matchers.equalTo(1280)))
                .andExpect(jsonPath("$[0].height", Matchers.equalTo(720)))
                .andExpect(jsonPath("$[0].hasExtra", Matchers.equalTo(1)))
                .andReturn()
                ;
        logger.debug("testGetTableArtifact1:" + result.getResponse().getContentAsString());

    }

    @Test
    @Order(3)
    void testGetTableWrongArtifact() throws Exception {
        this.mockMvc.perform(get("/api/media-file/table/8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @Order(4)
    void testGetTableIdNameDuration() throws Exception {
        this.mockMvc.perform(get("/api/media-file/table-id-name-duration/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0]", Matchers.aMapWithSize(3)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Name 11")))
                .andExpect(jsonPath("$[0].duration", Matchers.is(52345)))
                .andExpect(jsonPath("$[0].size").doesNotExist())
        ;
    }

    @Test
    @Order(5)
    void testGetTableFiles() throws Exception {
        this.mockMvc.perform(get("/api/media-file/table-files/4")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].text", Matchers.is("Tori Amos - Fade to Red Disk 1 2006.mkv")))
                .andExpect(jsonPath("$[1].text", Matchers.is("Tori Amos - Fade to Red Disk 2 2006.mkv")))
        ;
    }

    @Test
    @Order(5)
    void testGetMediaFileAttributes() throws Exception {
        this.mockMvc.perform(get("/api/media-file/file-attributes")
                        .param("artifactId", "99")
                        .param("mediaFileName", "Tori Amos - Fade to Red Disk 1 2006.mkv")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
        ;

        this.mockMvc.perform(get("/api/media-file/file-attributes")
                        .param("artifactId", "4")
                        .param("mediaFileName", "Tori Amos - Fade to Red Disk 1 2006.mkv1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
        ;

        this.mockMvc.perform(get("/api/media-file/file-attributes")
                        .param("artifactId", "4")
                        .param("mediaFileName", "Tori Amos - Fade to Red Disk 1 2006.mkv")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is("Tori Amos - Fade to Red Disk 1 2006.mkv")))
                .andExpect(jsonPath("$.format", Matchers.is("MKV")))
                .andExpect(jsonPath("$.size", Matchers.is(3032593)))
                .andExpect(jsonPath("$.bitrate", Matchers.is(600)))
                .andExpect(jsonPath("$.duration", Matchers.is(38)))
                .andExpect(jsonPath("$.width", Matchers.is(1280)))
                .andExpect(jsonPath("$.height", Matchers.is(720)))
                .andExpect(jsonPath("$.extra").doesNotExist())
        ;
    }

    @Test
    @Order(6)
    void testPostMediaFiles() throws Exception {
        this.mockMvc.perform(get("/api/media-file/table-id-name-duration/4")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));

        String json = mapper.writeValueAsString(List.of("Tori Amos - Fade to Red Disk 1 2006.mkv", "Tori Amos - Fade to Red Disk 2 2006.mkv"));
        logger.info("testPostMediaFiles json:{}", json);

        this.mockMvc.perform(post("/api/media-file/insert-media-files/97")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());

        String postResult = this.mockMvc.perform(post("/api/media-file/insert-media-files/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(1)))
                .andExpect(jsonPath("$.rowsAffected", Matchers.is(2)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        logger.info("testPostMediaFiles postResult:{}", postResult);

        this.mockMvc.perform(get("/api/media-file/table-id-name-duration/4")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }
}
