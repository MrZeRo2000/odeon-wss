package com.romanpulov.odeonwss.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AppConfiguration {

    public enum PathType {
        PT_MP3,
        PT_LA,
        PT_CLASSICS,
        PT_DV_MUSIC,
        PT_DV_MOVIES,
        PT_DV_ANIMATION,
        PT_MDB,
        PT_FFPROBE,
        PT_MEDIAINFO
    }

    protected final Map<PathType, String> pathMap = new HashMap<>();

    private final String version;

    public Map<PathType, String> getPathMap() {
        return pathMap;
    }

    public String getVersion() {
        return version;
    }

    public AppConfiguration(AppConfigurationProperties appConfigurationProperties,
                            ProjectConfigurationProperties projectConfigurationProperties) {
        this.pathMap.put(PathType.PT_MP3, appConfigurationProperties.getMp3Path());
        this.pathMap.put(PathType.PT_LA, appConfigurationProperties.getLaPath());
        this.pathMap.put(PathType.PT_CLASSICS, appConfigurationProperties.getClassicsPath());
        this.pathMap.put(PathType.PT_DV_MUSIC, appConfigurationProperties.getDvMusicPath());
        this.pathMap.put(PathType.PT_DV_MOVIES, appConfigurationProperties.getDvMoviesPath());
        this.pathMap.put(PathType.PT_DV_ANIMATION, appConfigurationProperties.getDvAnimationPath());
        this.pathMap.put(PathType.PT_MDB, appConfigurationProperties.getMdbPath());
        this.pathMap.put(PathType.PT_FFPROBE, appConfigurationProperties.getFfProbePath());
        this.pathMap.put(PathType.PT_MEDIAINFO, appConfigurationProperties.getMediaInfoPath());

        version = projectConfigurationProperties.getVersion();
    }
}
