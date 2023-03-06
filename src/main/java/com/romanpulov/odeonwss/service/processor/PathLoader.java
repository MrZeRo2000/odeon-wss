package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PathLoader {

    public interface ArtistArtifactPathLoader {
        void loadFromArtifactPath(Path artistPath, Path artifactPath, List<MediaFileValidationDTO> accumulator)
                throws ProcessorException;
    }

    public static List<MediaFileValidationDTO> loadFromPath(AbstractProcessor processor, Path path) throws ProcessorException {
        List<MediaFileValidationDTO> result = new ArrayList<>();

        List<Path> artifactPaths = new ArrayList<>();
        if (PathReader.readPathFoldersOnly(processor, path, artifactPaths)) {
            for (Path artifactPath: artifactPaths) {
                List<Path> trackPaths = new ArrayList<>();
                if (PathReader.readPathFilesOnly(processor, artifactPath, trackPaths)) {
                    trackPaths.forEach(trackPath ->
                            result.add(MediaFileValidationDTO.fromDVMediaFile(
                                    artifactPath.getFileName().toString(),
                                    null,
                                    trackPath.getFileName().toString())));
                } else {
                    break;
                }
            }
        }

        return result;
    }

    public static List<MediaFileValidationDTO> loadFromPathArtistArtifacts(
            AbstractProcessor processor,
            Path path,
            ArtistArtifactPathLoader loader) throws ProcessorException {
        List<MediaFileValidationDTO> result = new ArrayList<>();

        List<Path> artistPaths = new ArrayList<>();
        if (PathReader.readPathFoldersOnly(processor, path, artistPaths)) {
            for (Path artistPath: artistPaths) {
                List<Path> artifactPaths = new ArrayList<>();
                if (PathReader.readPathFoldersOnly(processor, artistPath, artifactPaths)) {
                    for (Path artifactPath: artifactPaths) {
                        loader.loadFromArtifactPath(artistPath, artifactPath, result);
                    }
                }
            }
        }

        return result;
    }
}
