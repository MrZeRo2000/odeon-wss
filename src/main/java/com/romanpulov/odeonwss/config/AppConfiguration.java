package com.romanpulov.odeonwss.config;

import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
public class AppConfiguration {

    private final String dbUrl;

    private final String mp3Path;

    private final String laPath;

    private final String classicsPath;

    private final String dvMusicPath;

    private final String mdbPath;

    private final String ffprobePath;

    private final String version;

    public String getDbUrl() {
        return dbUrl;
    }

    public String getMp3Path() {
        return mp3Path;
    }

    public String getLaPath() {
        return laPath;
    }

    public String getClassicsPath() {
        return classicsPath;
    }

    public String getDvMusicPath() {
        return dvMusicPath;
    }

    public String getMdbPath() {
        return mdbPath;
    }

    public String getFFProbePath() {
        return ffprobePath;
    }

    public String getVersion() {
        return version;
    }

    public AppConfiguration(ServletContext context, ProjectConfigurationProperties projectConfigurationProperties) {
        dbUrl = context.getInitParameter("db-url");
        mp3Path = context.getInitParameter("mp3-path");
        laPath = context.getInitParameter("la-path");
        classicsPath = context.getInitParameter("classics-path");
        dvMusicPath = context.getInitParameter("dv-music-path");
        mdbPath = context.getInitParameter("mdb-path");
        ffprobePath = context.getInitParameter("ffprobe-path");
        version = projectConfigurationProperties.getVersion();
    }
}
