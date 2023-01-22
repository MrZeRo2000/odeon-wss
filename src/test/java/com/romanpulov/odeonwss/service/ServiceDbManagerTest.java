package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.db.DbManagerService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled
public class ServiceDbManagerTest {

    @Autowired
    AppConfiguration appConfiguration;

    @Test
    @Order(1)
    void testDbPath() {
        DbManagerService dbManagerService = DbManagerService.getInstance(appConfiguration);
        Assertions.assertEquals("db/database/odeon-test.db", dbManagerService.getDbPath());
    }

    @Test
    @Order(2)
    void testCopyDb() throws Exception {
        DbManagerService dbManagerService = DbManagerService.getInstance(appConfiguration);

        final DbManagerService.DbType dbType = DbManagerService.DbType.DB_IMPORTED_MOVIES;

        Path storageFileNamePath = Paths.get(dbManagerService.getStorageFileName(dbType));
        if (Files.exists(storageFileNamePath)) {
            Files.delete(storageFileNamePath);
        }
        Assertions.assertFalse(Files.exists(storageFileNamePath));

        dbManagerService.saveDb(dbType);

        Assertions.assertTrue(Files.exists(storageFileNamePath));
    }

    @Test
    @Order(3)
    void testLoadSourceExists() throws Exception {
        DbManagerService dbManagerService = DbManagerService.getInstance(appConfiguration);
        Assertions.assertTrue(dbManagerService.loadDb(DbManagerService.DbType.DB_IMPORTED_MOVIES));
    }

    @Test
    @Order(4)
    void testLoadSourceNotExists() throws Exception {
        DbManagerService dbManagerService = DbManagerService.getInstance(appConfiguration);
        final DbManagerService.DbType dbType = DbManagerService.DbType.DB_IMPORTED_MOVIES;

        Files.deleteIfExists(Paths.get(dbManagerService.getStorageFileName(dbType)));

        Assertions.assertFalse(dbManagerService.loadDb(DbManagerService.DbType.DB_IMPORTED_MOVIES));
    }

}
