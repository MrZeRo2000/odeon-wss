package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LALoadProcessor extends AbstractArtistProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LALoadProcessor.class);

    public LALoadProcessor(ArtistRepository artistRepository, ArtifactRepository artifactRepository) {
        super(artistRepository, artifactRepository);
    }

    @Override
    protected void processCompositionsPath(Path path, Artifact artifact) throws ProcessorException {
        logger.debug("Processing composition path:" + path + " with artifact:" + artifact);

        try (Stream<Path> stream = Files.list(path)) {
            List<Path> directoryPaths = stream.collect(Collectors.toList());
            List<String> directoryFileNames = directoryPaths.stream().map(p -> p.getFileName().toString()).collect(Collectors.toList());

            //validation for Cue
            List<Path> cuePaths = directoryPaths.stream().filter(p -> p.toString().endsWith("cue")).collect(Collectors.toList());
            for (Path cuePath: cuePaths) {
                List<CueParser.CueTrack> cueTracks = CueParser.parseFile(cuePath);
                List<String> cueFiles = cueTracks
                        .stream()
                        .map(CueParser.CueTrack::getFileName)
                        .distinct().collect(Collectors.toList());

                if (new HashSet<>(directoryFileNames).containsAll(cueFiles)) {
                    logger.debug("Contains all for " + cuePath.toString());
                    processCueFile(path, artifact, cuePath, cueTracks, cueFiles);
                }
            }

        } catch (IOException e) {
            throw new ProcessorException(ProcessorMessages.ERROR_PROCESSING_FILES, e.getMessage());
        }
    }

    private void processCueFile(Path path, Artifact artifact, Path cuePath, List<CueParser.CueTrack> cueTracks, List<String> cueFiles) {
        logger.debug("Processing Cue");
    }
}
