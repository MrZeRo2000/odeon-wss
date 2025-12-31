package com.romanpulov.odeonwss.generator;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileSystemUtils;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Logger;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileTreeGeneratorTest {
    private static final Logger log = Logger.getLogger(FileTreeGeneratorTest.class.getSimpleName());

    @Value("${test.data.path}")
    String testDataPath;

    @Autowired
    private JsonMapper mapper;

    @Test
    void testDeserializeFolderDef() {
        var fd = new FileTreeGenerator.FolderDef("folder1", Map.of("file1", Paths.get("fileSource1")));
        String fdString = mapper.writeValueAsString(fd);
        log.info("Serialized FolderDef:" + fdString);
    }

    @Test
    void testSerializeFolderDef() {
        String fdString =
"""
{
    "folderName": "folder1",
    "files": {
        "file1": "fileSource1",
        "file2": "fileSource2"
    }
}
""";
        var fd = mapper.readValue(fdString, FileTreeGenerator.FolderDef.class);
        log.info("Deserialized FolderDef:" + fd);

        assertThat(fd.folderName()).isEqualTo("folder1");
        assertThat(fd.files().get("file1").toString()).contains("fileSource1");
        assertThat(fd.files().get("file2").toString()).contains("fileSource2");
    }

    @Test
    void testGenerateFileTree() throws Exception {
        String ftString =
"""
{
  "Pink Floyd": {
    "1977 Animals": {
      "01 Pigs On The Wing.flac": "sample_flac_1.flac",
      "02 Dogs.flac": "sample_flac_1.flac"
    }
  },
  "Pink Floyd - The Wall 1979": {
    "The Wall.mkv": "sample_1280x720_with_chapters.mkv"
  }
}
""";
        JSONObject jo = new JSONObject(ftString);
        Path path = Files.createTempDirectory("testGenerateFileTree");
        log.info("Created temp path:" + path.toString());
        try {
            processJSONKey(path, this.testDataPath, jo, null);
        } finally {
            log.info("Deleting temp path:" + path);
            FileSystemUtils.deleteRecursively(path);
        }
    }


    void processJSONKey(Path path, String testDataPath, JSONObject jo, String key) throws Exception {
        if (key == null) {
            jo.keySet().forEach(v -> {
                try {
                    processJSONKey(path, testDataPath, jo, v);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            Object ko = jo.get(key);
            Path keyPath = Path.of(path.toString(), key);
            if (ko instanceof String keyValue) {
                log.info("Found string: " + key + ":" + keyValue);
                Path sourcePath = Path.of(testDataPath, keyValue);
                if (!Files.exists(sourcePath)) {
                    throw new IOException("Source file not found:" + sourcePath);
                } else {
                    Files.copy(sourcePath, keyPath, REPLACE_EXISTING);
                }
            } else if (ko instanceof JSONObject) {
                log.info("Found JSONObject, key:" + key);
                Files.createDirectory(keyPath);
                processJSONKey(keyPath, testDataPath, (JSONObject) ko, null);
            }
        }
    }

}
