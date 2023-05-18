package com.romanpulov.odeonwss.config;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class BackupConfiguration {

    public static final String BACKUP_PATH = "backup";
    public static final String BACKUP_FILE = "%s-backup";
    private final String dbUrl;

    public String getDbFileName() {
        String[] splitDBUrl = dbUrl.split(":");
        return splitDBUrl[2];
    }

    public String getBackupPath() {
        Path backupFilePath = Path.of(getDbFileName());
        return Path.of(backupFilePath.getParent().toString(), BACKUP_PATH).toString();
    }

    public String getBackupFileName() {
        String fileName = Path.of(getDbFileName()).getFileName().toString();

        int pos = fileName.lastIndexOf(".");
        if (pos > 0 && pos < (fileName.length() - 1)) { // If '.' is not the first or last character.
            fileName = fileName.substring(0, pos);
        }

        return String.format(BACKUP_FILE, fileName);
    }

    public BackupConfiguration(DatabaseConfiguration databaseConfiguration) {
        this.dbUrl = databaseConfiguration.getDbUrl();
    }
}
