package com.romanpulov.odeonwss.service.processor.utils;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.service.processor.AbstractProcessor;
import com.romanpulov.odeonwss.service.processor.PathReader;
import com.romanpulov.odeonwss.service.processor.ProcessorException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathProcessUtil {

    private static Artist getArtistFromArtifactName(String artifactName, Map<String, Long> artists) {
        return artists
                .entrySet()
                .stream()
                .filter(v -> artifactName.startsWith(v.getKey()))
                .min(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .map(v -> {
                    Artist result = new Artist();
                    result.setId(v.getValue());
                    return result;
                })
                .orElse(null);
    }

    public static int processArtifactsPath(
            AbstractProcessor processor,
            Path path,
            Map<String, Long> artists,
            ArtifactRepository artifactRepository,
            ArtifactType artifactType,
            Function<String, Long> artifactYearProviderCallback,
            Consumer<String> processingArtifactCallback,
            Consumer<String> processingErrorExpectedDirectoryCallback) throws ProcessorException {
        if (!Files.isDirectory(path)) {
            processingErrorExpectedDirectoryCallback.accept(path.toString());
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
                        processingArtifactCallback.accept(artifactName);

                        Artifact artifact = new Artifact();
                        artifact.setArtifactType(artifactType);

                        if (artists != null) {
                            artifact.setArtist(PathProcessUtil.getArtistFromArtifactName(artifactName, artists));
                        }

                        artifact.setTitle(artifactName);
                        artifact.setDuration(0L);
                        if (artifactYearProviderCallback != null) {
                            artifact.setYear(artifactYearProviderCallback.apply(artifactName));
                        }

                        artifactRepository.save(artifact);
                        counter.getAndIncrement();
                    }
                });

        return counter.get();
    }
}
