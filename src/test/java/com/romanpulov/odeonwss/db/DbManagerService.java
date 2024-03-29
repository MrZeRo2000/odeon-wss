package com.romanpulov.odeonwss.db;

import com.romanpulov.odeonwss.config.DatabaseConfiguration;
import org.hibernate.cfg.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class DbManagerService {
    public static DbManagerService getInstance(DatabaseConfiguration databaseConfiguration) {
        return new DbManagerService(databaseConfiguration);
    }

    private static final String PROJECT_NAME;

    static {
        String[] location = DbManagerService.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toString()
                .split("/");
        if (location.length > 5) {
            PROJECT_NAME = location[location.length - 5];
        } else {
            PROJECT_NAME = "DEFAULT";
        }
    }

    private static final Logger log = Logger.getLogger(DbManagerService.class.getSimpleName());

    public enum DbType {
        DB_IMPORTED_MOVIES("imported_movies"),
        DB_IMPORTED_ARTISTS("imported_artists"),
        DB_LOADED_MOVIES("loaded_movies"),
        DB_LOADED_LA("loaded_la"),
        DB_LOADED_MP3("loaded_mp3"),
        DB_PRODUCTS("products"),
        DB_ARTISTS_DV_MUSIC_IMPORT("artists_dv_music"),
        DB_ARTISTS_DV_MUSIC_MEDIA("artists_dv_music_media"),
        //only those which are available in test data
        DB_ARTISTS_DV_MUSIC_MEDIA_EXISTING("artists_dv_music_existing"),;

        public final String fileName;

        DbType(String fileName) {
            this.fileName = fileName;
        }
    }

    private final String dbPath;

    public String getDbPath() {
        return dbPath;
    }

    public String getStorageFileName(DbType dbType) {
        return this.storageDbFolder + dbType.fileName;
    }

    private final String storageDbFolder;

    private DbManagerService(DatabaseConfiguration databaseConfiguration) {
        this.dbPath = databaseConfiguration.getDbUrl().split(":")[2];
        this.storageDbFolder = Environment.getProperties().get("java.io.tmpdir").toString() +
                PROJECT_NAME +
                "-db" +
                File.separator;
    }

    public void saveDb(DbType dbType) throws IOException {
        Files.createDirectories(Paths.get(this.storageDbFolder));
        Files.copy(Paths.get(this.dbPath), Paths.get(getStorageFileName(dbType)), REPLACE_EXISTING);
    }

    public boolean loadDb(DbType dbType) throws IOException {
        Path sourcePath = Paths.get(getStorageFileName(dbType));
        boolean sourceExists = Files.exists(sourcePath);
        if (sourceExists) {
            log.info("Loading database from " + getStorageFileName(dbType));
            Files.copy(Paths.get(getStorageFileName(dbType)), Paths.get(this.dbPath), REPLACE_EXISTING);
        }
        return sourceExists;
    }

    public static void loadOrPrepare(DatabaseConfiguration databaseConfiguration, DbType dbType, Preparable preparable) {
        DbManagerService dbManagerService = getInstance(databaseConfiguration);
        try {
            if (!dbManagerService.loadDb(dbType)) {
                preparable.prepare();
                dbManagerService.saveDb(dbType);
            } else {
                log.info("Successfully loaded " + dbType + " with " + databaseConfiguration.getDbUrl());
            }
        } catch (IOException e) {
            log.warning("Unable to load db " + dbType + ":" + e.getMessage() + ", running prepare");
            preparable.prepare();
            try {
                dbManagerService.saveDb(dbType);
            } catch (IOException ignored) {}
        }
    }
}
