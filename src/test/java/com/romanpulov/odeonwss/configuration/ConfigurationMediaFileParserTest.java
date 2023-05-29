package com.romanpulov.odeonwss.configuration;

import com.romanpulov.odeonwss.utils.media.MediaFileParserInterface;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfigurationMediaFileParserTest {

    @Autowired
    MediaFileParserInterface mediaFileParser;

    @Test
    void testParser() {
        assertThat(mediaFileParser).isNotNull();
        assertThat(
                Path.of(mediaFileParser.getExecutableFileName()).getFileName().toString())
                .isEqualTo("MediaInfo.exe");
    }
}
