package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTOBuilder;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
                List<Path> mediaFilePaths = new ArrayList<>();
                if (PathReader.readPathPredicateFilesOnly(
                        processor,
                        artifactPath,
                        p -> NamesParser.validateFileNameMediaFormat(p.getFileName().toString(), mediaFileFormats),
                        mediaFilePaths)) {
                    mediaFilePaths.forEach(mediaFilePath -> {
                        long mediaFileSize;
                        try {
                            mediaFileSize = Files.size(mediaFilePath);
                        } catch (IOException e) {
                            mediaFileSize = 0;
                        }
                        result.add(new MediaFileValidationDTOBuilder()
                                    .withArtifactTitle(artifactPath.getFileName().toString())
                                    .withMediaFileName(mediaFilePath.getFileName().toString())
                                    .withMediaFileSize(mediaFileSize)
                                    .build()
                            );
                        }
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
            ArtistArtifactPathLoader loader,
            Consumer<String> artistLoadingCallback
            ) throws ProcessorException {
        List<MediaFileValidationDTO> result = new ArrayList<>();

        List<Path> artistPaths = new ArrayList<>();
        if (PathReader.readPathFoldersOnly(processor, path, artistPaths)) {
            for (Path artistPath: artistPaths) {
                if (artistLoadingCallback != null) {
                    artistLoadingCallback.accept(artistPath.getFileName().toString());
                }
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
