package com.romanpulov.odeonwss;

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
        Assertions.assertEquals("D:/Temp/ok/MP3 Music/", appConfiguration.getMp3Path());
    }
}
