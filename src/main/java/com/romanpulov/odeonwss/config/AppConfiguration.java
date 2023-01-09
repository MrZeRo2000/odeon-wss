package com.romanpulov.odeonwss.config;

import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppConfiguration {

    public enum PathType {
        PT_MP3,
        PT_LA,
        PT_CLASSICS,
        PT_DV_MUSIC,
        PT_DV_MOVIE,
        PT_MDB,
        PT_FFPROBE
    }

    private static final Map<PathType, String> PATH_PARAMETER_MAP = Map.of(
            PathType.PT_MP3, "mp3-path",
            PathType.PT_LA, "la-path",
            PathType.PT_CLASSICS, "classics-path",
            PathType.PT_DV_MUSIC, "dv-music-path",
            PathType.PT_DV_MOVIE, "dv-movie-path",
            PathType.PT_MDB, "mdb-path",
            PathType.PT_FFPROBE, "ffprobe-path"
    );

    private final Map<PathType, String> pathMap = new HashMap<>();

    private final String dbUrl;

    private final String version;

    public String getDbUrl() {
        return dbUrl;
    }

    public Map<PathType, String> getPathMap() {
        return pathMap;
    }

    public String getVersion() {
        return version;
    }

    public AppConfiguration(ServletContext context, ProjectConfigurationProperties projectConfigurationProperties) {
        dbUrl = context.getInitParameter("db-url");

        AppConfiguration.PATH_PARAMETER_MAP.forEach(
                (key, value) -> this.pathMap.put(key, context.getInitParameter(value))
        );

        version = projectConfigurationProperties.getVersion();
    }
}
