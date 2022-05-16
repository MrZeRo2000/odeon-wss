package com.romanpulov.odeonwss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.util.Objects;

@Component
public class DatabaseConfiguration {
    private final DatabaseConfigurationProperties databaseConfigurationProperties;
    private final AppConfiguration appConfiguration;

    @Autowired
    public DatabaseConfiguration(AppConfiguration appConfiguration, DatabaseConfigurationProperties databaseConfigurationProperties) {
        this.appConfiguration = appConfiguration;
        this.databaseConfigurationProperties = databaseConfigurationProperties;
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(databaseConfigurationProperties.getDriverClassName()));
        dataSource.setUrl(appConfiguration.getDbUrl());
        dataSource.setUsername(databaseConfigurationProperties.getUsername());
        dataSource.setPassword(databaseConfigurationProperties.getPassword());
        return dataSource;
    }
}
