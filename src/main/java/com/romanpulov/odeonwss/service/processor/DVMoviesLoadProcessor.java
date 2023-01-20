package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.DVType;
import com.romanpulov.odeonwss.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DVMoviesLoadProcessor extends AbstractFileSystemProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DVMoviesLoadProcessor.class);
    private static final ArtifactType ARTIFACT_TYPE = ArtifactType.withDVMovies();

    private final ArtifactRepository artifactRepository;

    private final CompositionRepository compositionRepository;

    private final MediaFileRepository mediaFileRepository;

    private final DVTypeRepository dvTypeRepository;
    private final DVProductRepository dVProductRepository;

    public DVMoviesLoadProcessor(
            ArtifactRepository artifactRepository,
            CompositionRepository compositionRepository,
            MediaFileRepository mediaFileRepository,
            DVTypeRepository dvTypeRepository,
            DVProductRepository dVProductRepository) {
        this.artifactRepository = artifactRepository;
        this.compositionRepository = compositionRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.dvTypeRepository = dvTypeRepository;
        this.dVProductRepository = dVProductRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        infoHandler(ProcessorMessages.INFO_ARTIFACTS_LOADED, processArtifactsPath(path));
        infoHandler(ProcessorMessages.INFO_COMPOSITIONS_LOADED, processCompositions());
        infoHandler(ProcessorMessages.INFO_MEDIA_FILES_LOADED, processMediaFiles(path));
    }

    private int processArtifactsPath(Path path) throws ProcessorException {
        if (!Files.isDirectory(path)) {
            errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, path.getFileName());
            return 0;
        }

        AtomicInteger counter = new AtomicInteger(0);

        Map<String, Artifact > artifacts = this.artifactRepository.getAllByArtifactType(ARTIFACT_TYPE)
                .stream()
                .collect(Collectors.toMap(Artifact::getTitle, v -> v));

        List<String> artifactNames = new ArrayList<>();

        try (Stream<Path> stream = Files.list(path)) {
            for (Path p: stream.collect(Collectors.toList())) {
                if (!Files.isDirectory(p)) {
                    errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, path.getFileName());
                    return counter.get();
                }

                artifactNames.add(p.getFileName().toString());
            }
        } catch (IOException e) {
            throw new ProcessorException(ProcessorMessages.ERROR_PROCESSING_FILES, e.getMessage());
        }

        artifactNames.forEach(artifactName -> {
            if (!artifacts.containsKey(artifactName)) {
                Artifact artifact = new Artifact();
                artifact.setArtifactType(ArtifactType.withDVMovies());
                artifact.setTitle(artifactName);
                artifact.setDuration(0L);

                artifactRepository.save(artifact);
                counter.getAndIncrement();
            }
        });

        return counter.get();
    }

    private int processCompositions() {
        AtomicInteger counter = new AtomicInteger(0);
        DVType dvType = dvTypeRepository.getById(7L);

        this.artifactRepository.getAllByArtifactTypeWithCompositions(ARTIFACT_TYPE)
                .stream()
                .filter(a -> a.getCompositions().size() == 0)
                .forEach(artifact -> {
                    Composition composition = new Composition();
                    composition.setArtifact(artifact);
                    composition.setDvType(dvType);
                    composition.setTitle(artifact.getTitle());
                    composition.setDuration(artifact.getDuration());
                    dVProductRepository.getFirstByTitle(composition.getTitle()).ifPresent(p -> {
                        composition.setDvProducts(Set.of(p));
                    });

                    compositionRepository.save(composition);

                    artifact.getCompositions().add(composition);
                    artifactRepository.save(artifact);

                    counter.getAndIncrement();
                });

        return counter.get();
    }

    private int processMediaFiles(Path path) {
        AtomicInteger counter = new AtomicInteger(0);

        //artifactRepository.getAllByArtifactTypeWithCompositions(ARTIFACT_TYPE).f
        //compositionRepository.g

        return counter.get();
    }
}
