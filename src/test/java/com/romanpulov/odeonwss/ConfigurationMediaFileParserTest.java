package com.romanpulov.odeonwss;

import com.romanpulov.odeonwss.utils.media.MediaFileParserInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfigurationMediaFileParserTest {

    @Autowired
    MediaFileParserInterface mediaFileParser;

    @Test
    void testParser() {
        Assertions.assertNotNull(mediaFileParser);
    }
}
