package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.BackupConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceBackupTest {

    @Autowired
    BackupConfiguration backupConfiguration;

    @Autowired
    BackupService backupService;

    @Test
    void testFileNames() {
        assertThat(backupConfiguration.getDbFileName()).isIn("db/database/odeon-test.db", "db\\database\\odeon-test.db");
        assertThat(backupConfiguration.getBackupPath()).isIn("db/database/backup", "db\\database\\backup");
        assertThat(backupConfiguration.getBackupFileName()).isEqualTo("odeon-test-backup");
    }

    @Test
    void testBackup() throws Exception {
        backupService.removeBackups();

        var backupDatabaseInfo = backupService.getBackupDatabaseInfo();
        assertThat(backupDatabaseInfo.getBackupFileCount()).isEqualTo(0);

        var backupResult = backupService.createBackup();
        assertThat(backupResult.getMessage()).isEqualTo("odeon-test-backup");

        Path backupFilePath = Path.of(backupConfiguration.getBackupPath(), backupConfiguration.getBackupFileName()+ ".zip");
        assertThat(Files.exists(backupFilePath)).isTrue();
        assertThat(Files.size(backupFilePath)).isGreaterThan(0L);

        backupService.removeBackups();
    }
}
