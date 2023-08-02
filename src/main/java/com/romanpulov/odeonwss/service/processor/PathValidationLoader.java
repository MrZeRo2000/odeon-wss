package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTOBuilder;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PathValidationLoader {

    public interface ArtistArtifactPathLoader {
        void loadFromArtifactPath(Path artistPath, Path artifactPath, List<MediaFileValidationDTO> accumulator)
                throws ProcessorException;
    }

    public static List<MediaFileValidationDTO> loadFromPath(
            AbstractProcessor processor,
            Path path,
            String mediaFileFormats) throws ProcessorException {
        List<MediaFileValidationDTO> result = new ArrayList<>();

        List<Path> artifactPaths = new ArrayList<>();
        if (PathReader.readPathFoldersOnly(processor, path, artifactPaths)) {
            for (Path artifactPath: artifactPaths) {
                List<Path> trackPaths = new ArrayList<>();
                if (PathReader.readPathPredicateFilesOnly(
                        processor,
                        artifactPath,
                        p -> NamesParser.validateFileNameMediaFormat(p.getFileName().toString(), mediaFileFormats),
                        trackPaths)) {
                    trackPaths.forEach(trackPath ->
                            result.add(new MediaFileValidationDTOBuilder()
                                    .withArtifactTitle(artifactPath.getFileName().toString())
                                    .withMediaFileName(trackPath.getFileName().toString())
                                    .build()
                            )
                    );
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
