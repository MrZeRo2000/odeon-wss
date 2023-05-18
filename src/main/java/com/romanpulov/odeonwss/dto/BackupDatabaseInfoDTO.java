package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class BackupDatabaseInfoDTO {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime lastBackupDateTime;

    private final Integer backupFileCount;

    @JsonCreator
    public BackupDatabaseInfoDTO(
            @JsonProperty("lastBackupDateTime") LocalDateTime lastBackupDateTime,
            @JsonProperty("backupFileCount") Integer backupFileCount
    ) {
        this.lastBackupDateTime = lastBackupDateTime;
        this.backupFileCount = backupFileCount;
    }

    public LocalDateTime getLastBackupDateTime() {
        return lastBackupDateTime;
    }

    public Integer getBackupFileCount() {
        return backupFileCount;
    }

    @Override
    public String toString() {
        return "BackupDatabaseInfoDTO{" +
                "lastBackupDateTime=" + lastBackupDateTime +
                ", backupFileCount=" + backupFileCount +
                '}';
    }
}
