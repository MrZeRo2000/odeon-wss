package com.romanpulov.odeonwss.db;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.config.DatabaseConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class DbManagerService {
    private static final Logger log = Logger.getLogger(DbManagerService.class.getSimpleName());

    public enum DbType {
        DB_MOVIES
    }

    @Autowired
    DatabaseConfiguration databaseConfiguration;

    public void saveDb(DbType dbType) {

    }
}
