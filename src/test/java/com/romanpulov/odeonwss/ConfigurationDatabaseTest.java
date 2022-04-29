package com.romanpulov.odeonwss;

import com.romanpulov.odeonwss.config.DatabaseConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfigurationDatabaseTest {

    @Autowired
    DatabaseConfiguration databaseConfiguration;

    @Test
    void testDbUrl() throws Exception {
        Assertions.assertEquals("jdbc:sqlite:db/database/odeon-test.db", databaseConfiguration.dataSource().getConnection().getMetaData().getURL());
    }
}
