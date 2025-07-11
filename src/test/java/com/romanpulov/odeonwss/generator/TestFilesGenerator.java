package com.romanpulov.odeonwss.generator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestFilesGenerator {

    @Value("${test.files.path}")
    String testFilesPath;

    @Value("${test.data.path}")
    String testDataPath;

    @Value("classpath:definitions/mp3_music.json")
    Resource mp3MusicDefinition;

    @Value("classpath:definitions/lossless.json")
    Resource losslessDefinition;

    @Value("classpath:definitions/classics.json")
    Resource classicsDefinition;

    @Value("classpath:definitions/dv_music.json")
    Resource dvMusicDefinition;

    @Value("classpath:definitions/dv_movies.json")
    Resource dvMoviesDefinition;

    @Value("classpath:definitions/dv_animation.json")
    Resource dvAnimationDefinition;

    @Value("classpath:templates/odeon-int-wss.xml")
    Resource intWSSTemplate;

    @Value("classpath:templates/odeon-wss.xml")
    Resource wssTemplate;

    @BeforeAll
    public void setup() throws Exception {
        FileSystemUtils.deleteRecursively(new File(testFilesPath));
        Path path = Paths.get(testFilesPath);
        Files.createDirectories(path);
    }

    private void createAndGenerateFromJSON(String folderName, Resource definition) throws Exception {
        Path path = Paths.get(testFilesPath, folderName);
        Files.createDirectories(path);
        FileTreeGenerator.generateFromJSON(
                path,
                this.testDataPath,
                definition.getContentAsString(StandardCharsets.UTF_8));
    }

    @Test
    void generateTestFiles() throws Exception {
        createAndGenerateFromJSON(
                "MP3 Music",
                this.mp3MusicDefinition
        );
        createAndGenerateFromJSON(
                "Lossless",
                this.losslessDefinition
        );
        createAndGenerateFromJSON(
                "classics",
                this.classicsDefinition
        );
        createAndGenerateFromJSON(
                "dv_music",
                this.dvMusicDefinition
        );
        createAndGenerateFromJSON(
                "dv_movies",
                this.dvMoviesDefinition
        );
        createAndGenerateFromJSON(
                "dv_animation",
                this.dvAnimationDefinition
        );
    }

    @Test
    void generateWSS() throws Exception {
        String rootPath = Paths.get("").toAbsolutePath().getParent().toString()
                .replace("\\", "/");
        String oneDrive = System.getenv().getOrDefault("OneDrive", "")
                .replace("\\", "/");

        String intContent = intWSSTemplate.getContentAsString(StandardCharsets.UTF_8);
        String expandedIntContent = intContent.replace("{root}", rootPath);
        Path intPath = Paths.get(testFilesPath, "odeon-int-wss.xml");
        Files.writeString(intPath, expandedIntContent);

        String content = wssTemplate.getContentAsString(StandardCharsets.UTF_8);
        String expandedContent = content
                .replace("{root}", rootPath)
                .replace("{OneDrive}", oneDrive);
        Path path = Paths.get(testFilesPath, "odeon-wss.xml");
        Files.writeString(path, expandedContent);
    }
}
