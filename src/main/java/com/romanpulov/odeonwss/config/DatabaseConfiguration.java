package com.romanpulov.odeonwss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.util.Objects;

@Component
public class DatabaseConfiguration {
    private final Environment env;
    private final ServletContext context;

    @Autowired
    public DatabaseConfiguration(Environment env, ServletContext context) {
        this.env = env;
        this.context = context;
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("db.driverClassName")));
        dataSource.setUrl(context.getInitParameter("db-url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
        return dataSource;
    }
}
