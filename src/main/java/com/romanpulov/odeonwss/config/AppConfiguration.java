package com.romanpulov.odeonwss.config;

import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
public class AppConfiguration {

    private final String dbUrl;

    private final String mp3Path;

    private final String laPath;

    private final String mdbPath;

    public String getDbUrl() {
        return dbUrl;
    }

    public String getMp3Path() {
        return mp3Path;
    }

    public String getLaPath() {
        return laPath;
    }

    public String getMdbPath() {
        return mdbPath;
    }

    public AppConfiguration(ServletContext context) {
        dbUrl = context.getInitParameter("db-url");
        mp3Path = context.getInitParameter("mp3-path");
        laPath = context.getInitParameter("la-path");
        mdbPath = context.getInitParameter("mdb-path");
    }
}
