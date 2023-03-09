package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PathProcessUtil {
    public static int processArtifactsPath(
            AbstractProcessor processor,
            Path path,
            ArtifactRepository artifactRepository,
            ArtifactType artifactType) throws ProcessorException {
        if (!Files.isDirectory(path)) {
            processor.errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, path.getFileName());
            return 0;
        }

        AtomicInteger counter = new AtomicInteger(0);

        Map<String, Artifact> artifacts = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .collect(Collectors.toMap(Artifact::getTitle, v -> v));

        List<Path> artifactFiles = new ArrayList<>();
        if (!PathReader.readPathFoldersOnly(processor, path, artifactFiles)) {
            return counter.get();
        }

        artifactFiles
                .stream()
                .map(f -> f.getFileName().toString())
                .forEach(artifactName -> {
                    if (!artifacts.containsKey(artifactName)) {
                        Artifact artifact = new Artifact();
                        artifact.setArtifactType(artifactType);
                        artifact.setTitle(artifactName);
                        artifact.setDuration(0L);

                        artifactRepository.save(artifact);
                        counter.getAndIncrement();
                    }
                });

        return counter.get();
    }
}
