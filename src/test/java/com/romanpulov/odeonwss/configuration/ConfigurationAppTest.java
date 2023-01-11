package com.romanpulov.odeonwss.configuration;

import com.romanpulov.odeonwss.config.AppConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfigurationAppTest {

    @Autowired
    AppConfiguration appConfiguration;

    @Test
    void test() {
        Assertions.assertEquals(
                "../odeon-test-data/ok/MP3 Music/",
                appConfiguration.getPathMap().get(AppConfiguration.PathType.PT_MP3)
        );
    }

    @Test
    void testVersion() {
        Assertions.assertEquals("0.0.1-SNAPSHOT", appConfiguration.getVersion());
    }
}
