package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PathLoader {
    public static List<MediaFileValidationDTO> loadFromPath(AbstractProcessor processor, Path path) throws ProcessorException {
        List<MediaFileValidationDTO> result = new ArrayList<>();

        List<Path> artifactPaths = new ArrayList<>();
        if (PathReader.readPathFoldersOnly(processor, path, artifactPaths)) {
            for (Path artifactPath: artifactPaths) {
                List<Path> compositionPaths = new ArrayList<>();
                if (PathReader.readPathFilesOnly(processor, artifactPath, compositionPaths)) {
                    compositionPaths.forEach(compositionPath ->
                            result.add(MediaFileValidationDTO.fromDVMediaFile(
                                    artifactPath.getFileName().toString(),
                                    null,
                                    compositionPath.getFileName().toString())));
                } else {
                    break;
                }
            }
        }

        return result;
    }

}
