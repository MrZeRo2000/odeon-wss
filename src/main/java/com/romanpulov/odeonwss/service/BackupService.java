package com.romanpulov.odeonwss.service;

import com.romanpulov.jutilscore.io.FileUtils;
import com.romanpulov.jutilscore.storage.BackupUtils;
import com.romanpulov.odeonwss.config.BackupConfiguration;
import com.romanpulov.odeonwss.dto.MessageDTO;
import com.romanpulov.odeonwss.dto.BackupDatabaseInfoDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class BackupService {

    public static final int COPIES_COUNT = 20;
    private final BackupConfiguration backupConfiguration;

    public BackupService(BackupConfiguration backupConfiguration) {
        this.backupConfiguration = backupConfiguration;
        FileUtils.setFileKeepCopiesCount(COPIES_COUNT);
    }

    public void removeBackups() throws IOException {
        Path backupPath = Path.of(backupConfiguration.getBackupPath());
        if (Files.exists(backupPath)) {
            FileSystemUtils.deleteRecursively(backupPath);
        }
    }

    public MessageDTO createBackup() {
        String backupName = BackupUtils.createRollingLocalBackup(
                backupConfiguration.getDbFileName(),
                backupConfiguration.getBackupPath(),
                backupConfiguration.getBackupFileName()
        );

        return MessageDTO.fromMessage(backupName);
    }

    public BackupDatabaseInfoDTO getBackupDatabaseInfo() {
        int backupFileCount = 0;
        LocalDateTime lastBackupDateTime = null;

        File[] backupFiles = BackupUtils.getBackupFiles(backupConfiguration.getBackupPath());
        if ((backupFiles != null) && ((backupFileCount = backupFiles.length) > 0)) {
            List<File> fileList = Arrays.asList(backupFiles);
            Optional<Long> lastModified = fileList.stream().map(File::lastModified).max(Long::compareTo);

            Date lastModifiedDate = new Date(lastModified.get());
            lastBackupDateTime = lastModifiedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        return new BackupDatabaseInfoDTO(lastBackupDateTime, backupFileCount);
    }
}
