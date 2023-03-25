package com.romanpulov.odeonwss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.util.Objects;

@Component
public class DatabaseConfiguration {
    private final DatabaseConfigurationProperties databaseConfigurationProperties;

    private final String dbUrl;

    public String getDbUrl() {
        return dbUrl;
    }

    public DatabaseConfiguration(
            ServletContext context,
            DatabaseConfigurationProperties databaseConfigurationProperties) {
        this.databaseConfigurationProperties = databaseConfigurationProperties;
        dbUrl = context.getInitParameter("db-url");
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(databaseConfigurationProperties.getDriverClassName()));
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(databaseConfigurationProperties.getUsername());
        dataSource.setPassword(databaseConfigurationProperties.getPassword());
        return dataSource;
    }
}
