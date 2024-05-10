package com.romanpulov.odeonwss.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

public class FileTreeGeneratorTest {
    private static final Logger log = Logger.getLogger(FileTreeGeneratorTest.class.getSimpleName());

    @Test
    void testDeserializeFolderDef() throws Exception {
        var fd = new FileTreeGenerator.FolderDef("folder1", Map.of("file1", Paths.get("fileSource1")));
        var mapper = new ObjectMapper();
        String fdString = mapper.writeValueAsString(fd);
        log.info("Serialized FolderDef:" + fdString);
    }

    @Test
    void testSerializeFolderDef() throws Exception {
        String fdString = "{\"folderName\":\"folder1\",\"files\":{\"file1\":\"file:///C:/Users/r1525/prj/odeon-wss/fileSource1\"}}";

        var mapper = new ObjectMapper();
        var fd = mapper.readValue(fdString, FileTreeGenerator.FolderDef.class);
        log.info("Deserialized FolderDef:" + fd);

        assertThat(fd.folderName()).isEqualTo("folder1");
        assertThat(fd.files().get("file1").toString()).contains("fileSource1");
    }
}
