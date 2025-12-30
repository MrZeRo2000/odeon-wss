package com.romanpulov.odeonwss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfigurationProperties {
    private String dbUrl;
    private String mp3Path;
    private String laPath;
    private String classicsPath;
    private String dvMusicPath;
    private String dvMoviesPath;
    private String dvAnimationPath;
    private String ffProbePath;
    private String mediaInfoPath;
    private String mdbPath;

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getMp3Path() {
        return mp3Path;
    }

    public void setMp3Path(String mp3Path) {
        this.mp3Path = mp3Path;
    }

    public String getLaPath() {
        return laPath;
    }

    public void setLaPath(String laPath) {
        this.laPath = laPath;
    }

    public String getClassicsPath() {
        return classicsPath;
    }

    public void setClassicsPath(String classicsPath) {
        this.classicsPath = classicsPath;
    }

    public String getDvMusicPath() {
        return dvMusicPath;
    }

    public void setDvMusicPath(String dvMusicPath) {
        this.dvMusicPath = dvMusicPath;
    }

    public String getDvMoviesPath() {
        return dvMoviesPath;
    }

    public void setDvMoviesPath(String dvMoviesPath) {
        this.dvMoviesPath = dvMoviesPath;
    }

    public String getDvAnimationPath() {
        return dvAnimationPath;
    }

    public void setDvAnimationPath(String dvAnimationPath) {
        this.dvAnimationPath = dvAnimationPath;
    }

    public String getFfProbePath() {
        return ffProbePath;
    }

    public void setFfProbePath(String ffProbePath) {
        this.ffProbePath = ffProbePath;
    }

    public String getMediaInfoPath() {
        return mediaInfoPath;
    }

    public void setMediaInfoPath(String mediaInfoPath) {
        this.mediaInfoPath = mediaInfoPath;
    }

    public String getMdbPath() {
        return mdbPath;
    }

    public void setMdbPath(String mdbPath) {
        this.mdbPath = mdbPath;
    }
}
