package com.romanpulov.odeonwss.configuration;

import com.romanpulov.odeonwss.config.ProjectConfigurationProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfigurationProjectTest {

    @Autowired
    ProjectConfigurationProperties projectConfigurationProperties;

    @Test
    void testName() {
        Assertions.assertEquals("odeon-wss", projectConfigurationProperties.getName());
    }

    @Test
    void testVersion() {
        Assertions.assertNotNull(projectConfigurationProperties.getVersion());
        Assertions.assertTrue(
                projectConfigurationProperties.getVersion().matches("\\d+\\.\\d+\\.\\d+.*"),
                "Version should follow semantic versioning, but was: " + projectConfigurationProperties.getVersion()
        );
    }

    @Test
    void testDescription() {
        Assertions.assertNotNull(projectConfigurationProperties.getDescription());
        Assertions.assertFalse(
                projectConfigurationProperties.getDescription().contains("${"),
                "Description placeholder was not expanded by Gradle's processResources filtering: "
                        + projectConfigurationProperties.getDescription()
        );
    }
}
