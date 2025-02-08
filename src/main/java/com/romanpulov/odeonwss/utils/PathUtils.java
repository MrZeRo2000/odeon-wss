package com.romanpulov.odeonwss.utils;

import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import org.springframework.lang.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;

public class PathUtils {
    @Nullable
    public static Path getArtifactPath(
            Path rootPath,
            Long artifactTypeParentId,
            String artifactTitle,
            String artistName,
            Long year) {
        Path path;
        if ((artifactTypeParentId == 100L) && (artifactTitle != null) && (artistName != null) && (year != null)) {
            path = Path.of(
                    rootPath.toString(),
                    artistName,
                    NamesParser.formatMusicArtifact(year, artifactTitle));
        } else if ((artifactTypeParentId == 200L) && (artifactTitle != null)) {
            path = Path.of(
                    rootPath.toString(),
                    artifactTitle);
        } else {
            return null;
        }

        return (Files.exists(path)) ? path : null;
    }
}
