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
    private String dvOtherPath;
    private String ffProbePath;
    private String mediaInfoPath;
    private String mdbPath;

    private String resolvePropertyPath(String propertyPath) {
        String homePath = System.getProperty("user.home");
        return propertyPath.replace("{USER_HOME}", homePath).replace("\\", "/");
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = resolvePropertyPath(dbUrl);
    }

    public String getMp3Path() {
        return mp3Path;
    }

    public void setMp3Path(String mp3Path) {
        this.mp3Path = resolvePropertyPath(mp3Path);
    }

    public String getLaPath() {
        return laPath;
    }

    public void setLaPath(String laPath) {
        this.laPath = resolvePropertyPath(laPath);
    }

    public String getClassicsPath() {
        return classicsPath;
    }

    public void setClassicsPath(String classicsPath) {
        this.classicsPath = resolvePropertyPath(classicsPath);
    }

    public String getDvMusicPath() {
        return dvMusicPath;
    }

    public void setDvMusicPath(String dvMusicPath) {
        this.dvMusicPath = resolvePropertyPath(dvMusicPath);
    }

    public String getDvMoviesPath() {
        return dvMoviesPath;
    }

    public void setDvMoviesPath(String dvMoviesPath) {
        this.dvMoviesPath = resolvePropertyPath(dvMoviesPath);
    }

    public String getDvAnimationPath() {
        return dvAnimationPath;
    }

    public void setDvAnimationPath(String dvAnimationPath) {
        this.dvAnimationPath = resolvePropertyPath(dvAnimationPath);
    }

    public String getDvOtherPath() {
        return dvOtherPath;
    }

    public void setDvOtherPath(String dvOtherPath) {
        this.dvOtherPath = resolvePropertyPath(dvOtherPath);
    }

    public String getFfProbePath() {
        return ffProbePath;
    }

    public void setFfProbePath(String ffProbePath) {
        this.ffProbePath = resolvePropertyPath(ffProbePath);
    }

    public String getMediaInfoPath() {
        return mediaInfoPath;
    }

    public void setMediaInfoPath(String mediaInfoPath) {
        this.mediaInfoPath = resolvePropertyPath(mediaInfoPath);
    }

    public String getMdbPath() {
        return mdbPath;
    }

    public void setMdbPath(String mdbPath) {
        this.mdbPath = resolvePropertyPath(mdbPath);
    }
}
